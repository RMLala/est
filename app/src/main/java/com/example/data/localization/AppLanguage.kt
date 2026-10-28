package com.example.data.localization

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    FRANCAIS("fr", "Français", "🇫🇷"),
    MALAGASY("mg", "Malagasy", "🇲🇬")
}

object LanguageStrings {

    fun get(key: String, lang: AppLanguage): String {
        val fr = stringsFr[key] ?: key
        if (lang == AppLanguage.FRANCAIS) return fr
        return stringsMg[key] ?: fr
    }

    private val stringsFr = mapOf(
        "app_name" to "École du Saint Esprit",
        "home" to "Accueil",
        "bible" to "Sainte Bible",
        "sermons" to "Notes de Sermon",
        "preaching" to "Homilétique",
        "library" to "Bibliothèque",
        "prayers" to "Prières & Témoignages",
        "declarations" to "Déclarations de Foi",
        "dictionary" to "Dictionnaire Biblique",
        "reading_plans" to "Plans de Lecture",
        "assistant_gemini" to "Assistant IA Gemini",
        "search" to "Recherche",
        "profile" to "Profil & Cloud",
        "more" to "Plus",

        // Actions & Features
        "study_streak" to "jours d'affilée",
        "verse_of_day" to "Verset du Jour",
        "parallel_mode" to "Parallèle (3)",
        "bookmarks" to "Signets & Surlignages",
        "reading_plans_subtitle" to "Chronologique, Thématique, Saint-Esprit & Annuel",
        "assistant_subtitle" to "Votre conseiller pour l'exégèse, prédications & prières",
        "themes_and_fonts" to "Thèmes & Polices",
        "customize_appearance" to "Personnaliser l'apparence",
        "choose_theme" to "Thème visuel",
        "choose_font" to "Police de lecture",
        "choose_font_color" to "Couleur du texte",
        "choose_font_size" to "Taille de police",
        "testimonies_hub" to "Témoignages de Gloire",
        "all_testimonies" to "Tous les Témoignages",
        "prayers_tab" to "Requêtes de Prière",
        "miracle" to "Miracle & Surnaturel",
        "healing" to "Guérison Divine",
        "deliverance" to "Délivrance",
        "provision" to "Provision",
        "family" to "Famille & Restauration",
        "salvation" to "Conversion & Salut",
        "custom" to "Personnalisé",
        "ask_gemini" to "Demander à Gemini...",
        "gemini_welcome" to "Je suis votre assistant spirituel propulsé par Gemini. Comment puis-je vous aider dans votre étude, sermon ou prière ?",
        "ai_action_preaching" to "Plan de Prédication",
        "ai_action_exegesis" to "Exégèse & Grec/Hébreu",
        "ai_action_prayer" to "Conducteur de Prière",
        "ai_action_declaration" to "Décret Scripturaire"
    )

    private val stringsMg = mapOf(
        "app_name" to "Sekolin'ny Fanahy Masina",
        "home" to "Fandraisana",
        "bible" to "Baiboly Masina",
        "sermons" to "Fanamarihana Toriteny",
        "preaching" to "Fanomanana Toriteny",
        "library" to "Trano Fitehirizam-boky",
        "prayers" to "Vavaka & Fijoroana Vavolombelona",
        "declarations" to "Fiekem-pinoana Velona",
        "dictionary" to "Rakibolana Baiboly",
        "reading_plans" to "Teti-pamakiana Baiboly",
        "assistant_gemini" to "Mpanolo-tsaina Gemini IA",
        "search" to "Fikarohana Eran'ny Fampiharana",
        "profile" to "Mombamomba & Rahona",
        "more" to "Miroborobo Kokoa",

        // Actions & Features
        "study_streak" to "andro nisesy",
        "verse_of_day" to "Andinin-tsoratra Masina Androany",
        "parallel_mode" to "Mampitaha (3)",
        "bookmarks" to "Tsoritan-tsoratra & Voamarika",
        "reading_plans_subtitle" to "Ara-potoana, Ara-lohahevitra, Fanahy Masina & Isan-taona",
        "assistant_subtitle" to "Mpanolo-tsaina amin'ny fandalinana, toriteny ary vavaka",
        "themes_and_fonts" to "Endrika & Endri-tsoratra",
        "customize_appearance" to "Hanova ny fijery sy lokon'ny fampiharana",
        "choose_theme" to "Loko fototra",
        "choose_font" to "Endri-tsoratra hovakiana",
        "choose_font_color" to "Lokon'ny soratra",
        "choose_font_size" to "Haben'ny soratra",
        "testimonies_hub" to "Fijoroana Vavolombelon'ny Voninahitra",
        "all_testimonies" to "Ireo Fijoroana Vavolombelona Rehetra",
        "prayers_tab" to "Fangataham-bavaka",
        "miracle" to "Fahagagana & Herin'Andriamanitra",
        "healing" to "Fanasitranana Masina",
        "deliverance" to "Fanafahana amin'ny fatorana",
        "provision" to "Famatsiana & Fitahiana",
        "family" to "Fianakaviana & Fanarenana",
        "salvation" to "Famonjena & Fiantsoana",
        "custom" to "Namboarina manokana",
        "ask_gemini" to "Manontania an'i Gemini...",
        "gemini_welcome" to "Izaho no mpanampy ara-panahy tarihin'ny Gemini. Ahoana no azoko anampiana anao amin'ny fandalinana, toriteny na vavaka androany ?",
        "ai_action_preaching" to "Drafitra Toriteny",
        "ai_action_exegesis" to "Fandinihana Teny Grika/Hebreo",
        "ai_action_prayer" to "Fitararihana Vavaka",
        "ai_action_declaration" to "Fanambarana Finoana"
    )
}
