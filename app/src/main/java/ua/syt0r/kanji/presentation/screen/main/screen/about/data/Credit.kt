package ua.syt0r.kanji.presentation.screen.main.screen.about.data

import androidx.annotation.StringRes
import ua.syt0r.kanji.R

enum class Credit(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val license: Int,
    val url: String
) {

    KanjiVg(
        title = R.string.about_license_kanjivg_title,
        description = R.string.about_license_kanjivg_description,
        license = R.string.about_license_CCASA3,
        url = "https://kanjivg.tagaini.net/"
    ),
    KanjiDic(
        title = R.string.about_license_kanji_dic_title,
        description = R.string.about_license_kanji_dic_description,
        license = R.string.about_license_CCASA3,
        url = "http://www.edrdg.org/wiki/index.php/KANJIDIC_Project"
    ),
    Tanos(
        title = R.string.about_license_tanos_title,
        description = R.string.about_license_tanos_description,
        license = R.string.about_license_CCBY,
        url = "http://www.tanos.co.uk/jlpt/"
    ),
    JmDict(
        title = R.string.about_license_jm_dict_title,
        description = R.string.about_license_jm_dict_description,
        license = R.string.about_license_CCASA4,
        url = "https://www.edrdg.org/jmdict/j_jmdict.html"
    ),
    JmDictFurigana(
        title = R.string.about_license_jm_dict_furigana_title,
        description = R.string.about_license_jm_dict_furigana_description,
        license = R.string.about_license_CCASA4,
        url = "https://github.com/Doublevil/JmdictFurigana"
    ),

}
