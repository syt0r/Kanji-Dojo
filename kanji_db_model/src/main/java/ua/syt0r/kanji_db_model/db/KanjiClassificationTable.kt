package ua.syt0r.kanji_db_model.db

object KanjiClassificationTable {

    const val TABLE_NAME = "classification"

    const val KANJI_COLUMN = "kanji"
    const val CLASS_GROUP_COLUMN = "group"
    const val CLASS_COLUMN = "class"

    enum class Groups(val value: String) {
        JLPT("jlpt"),
        GRADE("grade")
    }

}