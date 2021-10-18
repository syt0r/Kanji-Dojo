import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

buildscript {

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.google.code.gson:gson:2.8.8")
    }

}

tasks.register("postCheckResultsToSlack") {

    doLast {

        val githubJson = System.getenv("GIT")
        val outcomesJson = System.getenv("OUTCOMES")
        val secretsJson = System.getenv("SECRETS")

        val secrets = Gson().fromJson(secretsJson, JsonObject::class.java)

        val slackToken = secrets.get("SLACK_BOT_TOKEN").asString
        val slackChannelId = secrets.get("SLACK_CHANNEL_ID").asString
        val jiraUrl = secrets.get("TICKET_BASE_LINK").asString

        val message = createMessage(githubJson, outcomesJson, jiraUrl)
        println(message)

        try {
            println("Publishing check result...")
            val response = publishCheckResult(slackToken, slackChannelId, message)
            println("Done, response[$response]")
        } catch (e: Exception) {
            println("Publishing failed, error: ${e.message}")
        }

    }

}

fun createMessage(githubJson: String, outcomesJson: String, jiraUrl: String): String {
    val gson = Gson()
    val github = gson.fromJson(githubJson, JsonObject::class.java)

    val pullRequest = github.getAsJsonObject("event")
        .getAsJsonObject("pull_request")

    val pullRequestTitle = pullRequest.get("title").asString
    val pullRequestUrl = pullRequest.get("html_url").asString

    val relatedJiraTickets = getTickets(pullRequestTitle)
    val formattedTicketsMessage = relatedJiraTickets
        .map { "<$jiraUrl$it|$it>" }
        .joinToString(", ")

    val assignee = pullRequest.get("assignee")
        .takeIf { it.isJsonObject }
        ?.asJsonObject
        ?.get("login")
        ?.asString

    val reviewers = pullRequest.getAsJsonArray("requested_reviewers")
        .map { it.asJsonObject.get("login").asString }
        .joinToString(", ")

    val commitsCount = pullRequest.get("commits").asLong

    val pullRequestAvatar = pullRequest.getAsJsonObject("user").get("avatar_url").asString

    val testOutcomesMessage = getStepsOutcome(outcomesJson)
        .filter { it.key.contains("test") }
        .map { (stepId, isSuccessful) ->
            val formattedStepId = stepId.removeSuffix("-tests")
                .split("-")
                .map { it.capitalize() }
                .joinToString(" ")
            val icon = if (isSuccessful) ":white_check_mark:" else ":x:"
            "${formattedStepId}: $icon"
        }
        .joinToString("\n")

    val blocks = Blocks {

        section(
            text = Text.MarkdownText(
                """
                Please review my PR. If you do this fast, I will give you the huge Kudos :heart:
                """.trimIndent()
            )
        )

        divider()

        section(
            text = Text.MarkdownText(
                """
                    <$pullRequestUrl|$pullRequestTitle>
                    Assignee: $assignee
                    List of reviewers: $reviewers
                    Jira tickets: $formattedTicketsMessage
                    Commits count: $commitsCount
                """.trimIndent()
            ),
            accessory = Accessory.ImageAccessory(
                url = pullRequestAvatar,
                altText = "Avatar"
            )
        )

        divider()

        section(
            text = Text.MarkdownText("Tests execution results:\n $testOutcomesMessage")
        )

    }

    return blocks.toString()
}

fun getTickets(text: String): List<String> {
    val regex = Regex("AUGL-[0-9]*")
    return regex.findAll(text).map { it.value }.toList()
}

fun getStepsOutcome(outcomesJson: String): Map<String, Boolean> {
    val json = Gson().fromJson(outcomesJson, JsonObject::class.java)
    return json.entrySet()
        .map { (stepId, stepInfo) ->
            stepId to (stepInfo.asJsonObject.getAsJsonPrimitive("outcome").asString == "success")
        }
        .toMap()
}

fun publishCheckResult(slackToken: String, channelId: String, message: String): String {
    val connection = URL("https://slack.com/api/chat.postMessage")
        .openConnection() as HttpsURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Authorization", "Bearer $slackToken")
    connection.setRequestProperty("Content-type", "application/json")
    connection.doOutput = true

    val requestDataJson = JsonObject()
    requestDataJson.addProperty("channel", channelId)
    requestDataJson.addProperty("blocks", message)

    val data = requestDataJson.toString()
    connection.outputStream.write(data.toByteArray(Charsets.UTF_8))

    val responseCode = connection.responseCode
    println("Request successfully sent, response code: $responseCode")

    val response = StringBuilder()
    var line: String? = null
    val inputStream = if (responseCode < 400) connection.inputStream else connection.errorStream
    val reader = BufferedReader(InputStreamReader(inputStream))

    do {
        line = reader.readLine()
        line?.also { response.append(line) }
    } while (line != null)
    reader.close()

    return response.toString()
}


class Blocks(init: Blocks.() -> Unit) {

    private val jsonArray = JsonArray()

    init {
        init()
    }

    fun section(text: Text, accessory: Accessory? = null) {

        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "section")

        val textObj = JsonObject()
        textObj.addProperty("type", text.type)
        textObj.addProperty("text", text.text)
        jsonObject.add("text", textObj)

        if (accessory != null) {
            val accessoryObj = JsonObject()
            accessoryObj.addProperty("type", accessory.type)
            when (accessory) {
                is Accessory.ImageAccessory -> {
                    accessoryObj.addProperty("image_url", accessory.url)
                    accessoryObj.addProperty("alt_text", accessory.altText)
                }
            }
            jsonObject.add("accessory", accessoryObj)
        }

        jsonArray.add(jsonObject)
    }

    fun divider() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("type", "divider")
        jsonArray.add(jsonObject)
    }

    override fun toString(): String {
        return jsonArray.toString()
    }

}

sealed class Accessory(val type: String) {
    class ImageAccessory(
        val url: String,
        val altText: String
    ) : Accessory("image")
}

sealed class Text(
    val type: String,
    val text: String
) {

    class MarkdownText(text: String) : Text("mrkdwn", text)

}