package ua.syt0r.kanji.presentation.screen.main.screen.about

import ua.syt0r.kanji.presentation.common.resources.string.StringResolveScope

enum class AboutCredit(
    val title: StringResolveScope,
    val description: StringResolveScope,
    val license: StringResolveScope,
    val url: String
) {

    KanjiVg(
        title = { aboutLicenseKanjiVgTitle },
        description = { aboutLicenseKanjiVgDescription },
        license = { aboutLicenseCCASA3 },
        url = "https://kanjivg.tagaini.net/"
    ),
    KanjiDic(
        title = { aboutLicenseKanjiDicTitle },
        description = { aboutLicenseKanjiDicDescription },
        license = { aboutLicenseCCASA3 },
        url = "http://www.edrdg.org/wiki/index.php/KANJIDIC_Project"
    ),
    Tanos(
        title = { aboutLicenseTanosTitle },
        description = { aboutLicenseTanosDescription },
        license = { aboutLicenseCCBY },
        url = "http://www.tanos.co.uk/jlpt/"
    ),
    JmDict(
        title = { aboutLicenseJmDictTitle },
        description = { aboutLicenseJmDictDescription },
        license = { aboutLicenseCCASA4 },
        url = "https://www.edrdg.org/jmdict/j_jmdict.html"
    ),
    JmDictFurigana(
        title = { aboutLicenseJmDictFuriganaTitle },
        description = { aboutLicenseJmDictFuriganaDescription },
        license = { aboutLicenseCCASA4 },
        url = "https://github.com/Doublevil/JmdictFurigana"
    ),
    LeedsCorpus(
        title = { aboutLicenseLeedsCorpusTitle },
        description = { aboutLicenseLeedsCorpusDescription },
        license = { aboutLicenseCCBY },
        url = "http://corpus.leeds.ac.uk/list.html"
    )

}
