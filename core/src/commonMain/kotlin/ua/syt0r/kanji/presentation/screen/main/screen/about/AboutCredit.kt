package ua.syt0r.kanji.presentation.screen.main.screen.about

import ua.syt0r.kanji.presentation.common.resources.string.StringResolveScope

enum class AboutCredit(
    val title: StringResolveScope<String>,
    val description: StringResolveScope<String>,
    val license: StringResolveScope<String>,
    val url: String
) {

    KanjiVg(
        title = { about.licenseKanjiVgTitle },
        description = { about.licenseKanjiVgDescription },
        license = { about.licenseCCASA3 },
        url = "https://kanjivg.tagaini.net/"
    ),
    KanjiDic(
        title = { about.licenseKanjiDicTitle },
        description = { about.licenseKanjiDicDescription },
        license = { about.licenseCCASA3 },
        url = "http://www.edrdg.org/wiki/index.php/KANJIDIC_Project"
    ),
    Tanos(
        title = { about.licenseTanosTitle },
        description = { about.licenseTanosDescription },
        license = { about.licenseCCBY },
        url = "http://www.tanos.co.uk/jlpt/"
    ),
    JmDict(
        title = { about.licenseJmDictTitle },
        description = { about.licenseJmDictDescription },
        license = { about.licenseCCASA4 },
        url = "https://www.edrdg.org/jmdict/j_jmdict.html"
    ),
    JmDictFurigana(
        title = { about.licenseJmDictFuriganaTitle },
        description = { about.licenseJmDictFuriganaDescription },
        license = { about.licenseCCASA4 },
        url = "https://github.com/Doublevil/JmdictFurigana"
    ),
    LeedsCorpus(
        title = { about.licenseLeedsCorpusTitle },
        description = { about.licenseLeedsCorpusDescription },
        license = { about.licenseCCBY },
        url = "http://corpus.leeds.ac.uk/list.html"
    ),
    WanakanaKt(
        title = { about.licenseWanakanaKtTitle },
        description = { about.licenseWanakanaKtDescription },
        license = { about.licenseMIT },
        url = "https://github.com/esnaultdev/wanakana-kt"
    )

}
