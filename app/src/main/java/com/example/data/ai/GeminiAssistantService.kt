package com.example.data.ai

import com.example.BuildConfig
import com.example.data.localization.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
    USER, ASSISTANT
}

object GeminiAssistantService {

    private const val MODEL_NAME = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateAssistantResponse(
        history: List<ChatMessage>,
        userPrompt: String,
        language: AppLanguage
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val isRealKey = apiKey.isNotBlank() &&
                apiKey != "MY_GEMINI_API_KEY" &&
                !apiKey.startsWith("YOUR_")

        if (!isRealKey) {
            // Provide high-quality spiritual & exegetical fallback
            return@withContext generateLocalSpiritualFallback(userPrompt, language)
        }

        try {
            val systemInstruction = if (language == AppLanguage.MALAGASY) {
                """
                Ianao dia Mpanolo-tsaina ara-panahy sy mpampianatra teolojika ao amin'ny 'Sekolin'ny Fanahy Masina'.
                Mpanampy mahatoky ho an'ny mpianatra Baiboly, mpitory filazantsara ary mpivavaka ianao.
                Mahay mandinika ny teny fototra Grika sy Hebreo ianao, mahay manolotra drafitra toriteny voarafitra tsara (Homilétique),
                manentana amin'ny fiekem-pinoana velona ary mitarika amin'ny vavaka mafana ao amin'ny Fanahy Masina.
                Valio amin'ny fiteny Malagasy manan-kaja, feno fahasoavana sy herin'ny Soratra Masina ny fanontaniana rehetra.
                """.trimIndent()
            } else {
                """
                Vous êtes un Conseiller Spirituel et Théologien oint de l'« École du Saint Esprit ».
                Vous assistez les étudiants bibliques, prédicateurs et croyants avec :
                1. Des exégèses profondes (racines hébraïques et grecques de la Septante et du Nouveau Testament).
                2. Des plans de prédication homilétiques structurés et christocentriques.
                3. Des prières inspirées par l'Esprit et des proclamations de foi vivante.
                Répondez avec révélation, clarté biblique et onction du Saint-Esprit en français soutenu et chaleureux.
                """.trimIndent()
            }

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray()

                // Add past 4 messages for context
                history.takeLast(4).forEach { msg ->
                    val role = if (msg.sender == MessageSender.USER) "user" else "model"
                    contentsArray.put(JSONObject().apply {
                        put("role", role)
                        put("parts", JSONArray().put(JSONObject().put("text", msg.text)))
                    })
                }

                // Add current prompt
                contentsArray.put(JSONObject().apply {
                    put("role", "user")
                    put("parts", JSONArray().put(JSONObject().put("text", userPrompt)))
                })

                put("contents", contentsArray)

                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().put(JSONObject().put("text", systemInstruction)))
                })

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                    put("topK", 40)
                })
            }

            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"
            val requestBody = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotBlank()) {
                            return@withContext text
                        }
                    }
                }
            }

            // Fallback if API returned error or empty
            generateLocalSpiritualFallback(userPrompt, language)
        } catch (e: Exception) {
            generateLocalSpiritualFallback(userPrompt, language)
        }
    }

    private fun generateLocalSpiritualFallback(prompt: String, language: AppLanguage): String {
        val lower = prompt.lowercase()

        val isMalagasy = language == AppLanguage.MALAGASY

        // 1. Homiletics / Sermon Outline
        if (lower.contains("prédication") || lower.contains("sermon") || lower.contains("plan") || lower.contains("toriteny") || lower.contains("drafitra")) {
            return if (isMalagasy) {
                """
                📖 **Drafitra Toriteny Homiletika : Ny Hery sy Asan'ny Fanahy Masina**
                *Teny Fototra : Asan'ny Apostoly 1:8 & Jaona 14:16-17*

                **I. Fampidirana (Exorde)**
                - Ny filàn'ny fiangonana ankehitriny ny herin'ny Fanahy Masina fa tsy fahendren'olombelona fotsiny.
                - Ilay fanomezana nampanantenain'ny Ray ho an'ny mpino rehetra.

                **II. Hevi-dehibe 3 (Famelabelarana)**
                1. **Ilay Mpananatra Mitoetra (Jaona 14:16) :**
                   - Teny Grika: *Paraklētos* (Ilay antsoina hiara-dia sy hiaro).
                   - Tsy mpitsidika mandalo Izy fa monina ao am-ponao mandrakizay.
                2. **Ny Fandraisana ny Hery any Ambony (Asa 1:8) :**
                   - Teny Grika: *Dunamis* (Hery mahagaga mampahomby).
                   - Fanafahana amin'ny tahotra sy fahasahiana mijoro ho vavolombelon'i Kristy.
                3. **Ny Vokatry ny Fanahy eo amin'ny Fiainana (Galatiana 5:22) :**
                   - Ny toetran'i Kristy miseho amin'ny fitiavana, fifaliana, ary fiadanana.

                **III. Famaranana sy Antso ho amin'ny Vavaka**
                - Fanapahan-kevitra hanolo-tena hofehezin'ny Fanahy Masina.
                - Vavaka mangataka famenoana vaovao sy fandatsahana afo masina.
                """.trimIndent()
            } else {
                """
                📖 **Plan de Prédication Homilétique : La Puissance Agissante du Saint-Esprit**
                *Passage Clé : Actes 1:8 & Jean 14:16-18*

                **I. Introduction**
                - Le besoin impérieux de l'Onction divine pour l'Église face aux défis contemporains.
                - La promesse infaillible du Père donnée avant l'Ascension.

                **II. Corps du Sermon (3 Piliers)**
                1. **La Présence Continuelle du Consolateur (Jean 14:16) :**
                   - Racine grecque : *Paraklētos* (L'Avocat, le Guide appelé à nos côtés).
                   - Il n'est pas une simple influence mais une Personne divine qui demeure en nous.
                2. **Le Revêtement de la Puissance d'en Haut (Actes 1:8) :**
                   - Racine grecque : *Dunamis* (Capacité explosive et miraculeuse).
                   - Victoire sur la peur et autorité spirituelle pour proclamer le Christ.
                3. **La Manifestation du Fruit et des Dons (1 Cor 12 / Galates 5) :**
                   - Équilibre parfait entre le caractère saint et l'exercice des dons surnaturels.

                **III. Conclusion & Appel à la Prière**
                - Invitation à abandonner le contrôle charnel à l'Esprit.
                - Ministère de prière pour le baptême de feu et le renouveau intérieur.
                """.trimIndent()
            }
        }

        // 2. Exegesis / Hebrew / Greek
        if (lower.contains("grec") || lower.contains("hebreu") || lower.contains("exégèse") || lower.contains("lexique") || lower.contains("hebreo") || lower.contains("grika")) {
            return if (isMalagasy) {
                """
                🔍 **Fandalinana Teny Fototra (Exégèse Théologique)**

                1. **רוּחַ (Rûaḥ) - Hebreo :**
                   - Dikany: Rivotra, fofon'aina, Fanahy Masina (Genesisy 1:2).
                   - Ny Rûaḥ no nitsoka teo ambonin'ny rano tamin'ny famoronana ary manome aina ny nofo rehetra.

                2. **Παράκλητος (Paraklētos) - Grika :**
                   - Dikany: Mpananatra, mpisolovava, mpiaro, mpanohana eo anilanao (Jaona 14:16).
                   - Mampahatsiahy ny tenin'i Kristy rehetra sy mitarika ho amin'ny marina.

                3. **Δύναμις (Dunamis) - Grika :**
                   - Dikany: Hery mahavita fahagagana, hery ara-panahy mahery vaika (Asa 1:8).
                   - Avy amin'io teny io ny hoe dinamita. Hery manova ny tsy azo atao ho azo atao.

                4. **Πίστις (Pistis) - Grika :**
                   - Dikany: Finoana velona, fahatokiana feno, fanoloran-tena tsy mivadika (Hebreo 11:1).
                """.trimIndent()
            } else {
                """
                🔍 **Exégèse des Termes Originaux de la Révélation**

                1. **רוּחַ (Rûaḥ) - Hébreu (Ancien Testament) :**
                   - Signification : Souffle vivifiant, vent puissant, Esprit de Dieu (Genèse 1:2, Ézéchiel 37).
                   - Il symbolise l'énergie divine créatrice et l'onction prophétique souveraine.

                2. **Παράκλητος (Paraklētos) - Grec (Jean 14, 15, 16) :**
                   - Signification : Avocat, Défenseur, Intercesseur, Consolateur appelé auprès de soi.
                   - Christ est le premier Paraclet ; le Saint-Esprit est « un autre (allos - de même nature) Consolateur ».

                3. **Δύναμις (Dunamis) - Grec (Nouveau Testament) :**
                   - Signification : Puissance inhérente, capacité miraculeuse dynamique (Actes 1:8, Romains 1:16).
                   - Source des guérisons et des prodiges apostoliques.

                4. **Ζωή (Zōē) - Grec :**
                   - La vie divine, absolue, éternelle et incréée reçue par la nouvelle naissance (Jean 10:10).
                """.trimIndent()
            }
        }

        // 3. Prayer Guidance
        if (lower.contains("prière") || lower.contains("vavaka") || lower.contains("intercession")) {
            return if (isMalagasy) {
                """
                🕊️ **Fitararihana Vavaka ao amin'ny Fanahy Masina**

                « Ray any an-danitra ô, amin'ny Anaran'i Jesosy Kristy Zanakao :
                Misaotra Anao aho tamin'ny nanomezanao ahy ny Fanahy Masina, ilay Mpananatra sy Mpitarika ahy.
                Amin'ity andro ity, manaiky aho hofehezin'ny Fanahinao.
                Fenoy afo masina sy fahendrena any ambony ny foko sy ny saiko.
                Vonoy izay rehetra tebiteby, tahotra na fisalasalana.
                Ampio aho handre mazava tsara ny feonao sy hankatò ny sitraponao.
                Aoka ny herinao hanasitrana, hanafaka ary hitahy amin'ny alalako.
                Amena ! »
                """.trimIndent()
            } else {
                """
                🕊️ **Conducteur de Prière dans la Présence du Saint-Esprit**

                « Père Céleste, au Nom Puissant et Glorieux de Jésus-Christ :
                Je m'approche avec assurance devant le Trône de Ta Grâce.
                Saint-Esprit, Toi qui es le Consolateur, je T'invite à saturer mon être tout entier.
                Renouvelle en moi la flamme de la prière et la ferveur pour Ta Parole.
                Que toute forteresse de doute et d'anxiété s'écroule maintenant au Nom de Jésus.
                Revêts-moi de Ta force souveraine (Dunamis) afin que je sois un canal d'amour, de foi et de délivrance pour ceux qui m'entourent.
                Que Ta paix qui surpasse toute intelligence garde mon cœur aujourd'hui.
                Amen ! »
                """.trimIndent()
            }
        }

        // 4. Faith Declaration
        if (lower.contains("déclaration") || lower.contains("foi") || lower.contains("décret") || lower.contains("finoana") || lower.contains("fanambarana")) {
            return if (isMalagasy) {
                """
                🔥 **Fanambarana Finoana sy Fahefana ao amin'i Kristy**

                - « Izaho dia voavotra amin'ny ran'i Jesosy Kristy ary zanak'Andriamanitra velona ! » (Romana 8:16)
                - « Ilay ao anatiko dia lehibe noho ilay eo amin'izao tontolo izao ! » (1 Jaona 4:4)
                - « Mahay ny zavatra rehetra aho ao amin'Ilay mampahatanjaka ahy ! » (Filipiana 4:13)
                - « Ny aretina sy ny tahotra dia tsy manana anjara toerana ao amin'ny fiainako, satria ny diam-kapony no efa nahasitrana ahy ! » (Isaia 53:5)
                - « Ny Fanahy Masina no mitarika ny diako amin'ny fahazavana sy fahombiazana isan'andro ! »
                """.trimIndent()
            } else {
                """
                🔥 **Décrets de Foi & d'Autorité Spirituelle en Christ**

                - « Je suis racheté par le sang précieux de l'Agneau et justifié par la foi ! » (Romains 5:1)
                - « Celui qui est en moi est infiniment plus grand que celui qui est dans le monde ! » (1 Jean 4:4)
                - « Par les meurtrissures de Jésus, j'ai été pleinement guéri et restauré ! » (1 Pierre 2:24)
                - « Aucune arme forgée contre ma vie et mon appel ne pourra prospérer ! » (Ésaïe 54:17)
                - « Le Saint-Esprit déverse en moi l'amour, la puissance et la sagesse divine ! » (2 Timothée 1:7)
                """.trimIndent()
            }
        }

        // Default response
        return if (isMalagasy) {
            """
            🕊️ **Toro-hevitra avy amin'ny Mpanolo-tsaina Gemini :**

            Misaotra anao nanontany ! Ho an'ny fanontanianao momba ny « $prompt » :

            Ny Soratra Masina dia manambara fa ny Fanahy Masina no mampianatra ny zavatra rehetra sy mampahatsiahy izay rehetra nolazain'i Jesosy (Jaona 14:26).

            **Hevi-dehibe 3 ho anao :**
            1. **Fandinihana :** Avereno vakiana am-bavaka ny teny manodidina ao amin'ny Baiboly.
            2. **Fampiharana :** Avelao ny Fanahy Masina hampiseho ny fomba iainana izany anio.
            3. **Fiekem-pinoana :** Ambarao am-bava ny fandresena efa nomena anao ao amin'i Kristy.

            Inona no fanampiana manokana ilainao manaraka : Drafitra toriteny, fanazavana teny Grika/Hebreo, sa vavaka ?
            """.trimIndent()
        } else {
            """
            🕊️ **Conseil du Guide Spirituel Gemini :**

            Merci pour votre question inspirée concernant : « $prompt ».

            La Parole de Dieu déclare dans Jean 14:26 que le Saint-Esprit nous enseigne toutes choses et nous conduit dans toute la vérité.

            **Recommandations pour votre étude :**
            1. **Méditation approfondie :** Examinez le contexte scripturaire dans les 3 versions bibliques (Malagasy, Semeur, Amplified).
            2. **Approche Christocentrique :** Chaque passage biblique pointe vers la gloire et la rédemption en Jésus-Christ.
            3. **Libération par l'Esprit :** Proclamez cette vérité dans vos prières et vos sermons.

            Souhaitez-vous approfondir un point précis : exégèse hébraïque/grecque, schéma de sermon, ou décret de foi ?
            """.trimIndent()
        }
    }
}
