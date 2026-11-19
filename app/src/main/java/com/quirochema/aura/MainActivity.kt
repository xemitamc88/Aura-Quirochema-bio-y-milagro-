
package com.quirochema.aura

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*
import android.text.TextWatcher
import android.text.Editable
import org.json.JSONObject
import org.json.JSONArray

class MainActivity : Activity() {

    private val ink = Color.rgb(35, 43, 52)
    private val teal = Color.rgb(53, 157, 166)
    private val navy = Color.rgb(23, 34, 57)
    private val paper = Color.rgb(247, 249, 250)
    private val muted = Color.rgb(110, 120, 130)

    private lateinit var content: LinearLayout
    private lateinit var search: EditText
    private lateinit var library: JSONObject

    private var currentSection = "Inicio"
    private var loadError = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = navy
        window.navigationBarColor = navy

        loadLibrary()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(paper)
        }

        // CABECERA
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(20), dp(24), dp(20), dp(22))
            background = rounded(navy, 0, 0)
        }

        val logo = TextView(this).apply {
            text = "AURA"
            textSize = 29f
            setTextColor(Color.WHITE)
            typeface = Typeface.create("sans-serif", Typeface.BOLD)
            gravity = Gravity.CENTER
            letterSpacing = 0.16f
        }

        val subtitle = TextView(this).apply {
            text = "QUIROCHEMA"
            textSize = 13f
            setTextColor(Color.rgb(120, 205, 205))
            gravity = Gravity.CENTER
            letterSpacing = 0.22f
        }

        val description = TextView(this).apply {
            text = "Biblioteca holística de consulta y reflexión"
            textSize = 12f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, 0)
        }

        header.addView(logo)
        header.addView(subtitle)
        header.addView(description)

        root.addView(header)

        // BUSCADOR
        val searchContainer = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(8))
        }

        search = EditText(this).apply {
            hint = "Buscar aura, emoción, chakra..."
            textSize = 14f
            singleLine = true
            setPadding(dp(14), dp(10), dp(14), dp(10))
            background = rounded(Color.WHITE, dp(12), Color.rgb(225, 230, 233))
        }

        searchContainer.addView(
            search,
            LinearLayout.LayoutParams(0, dp(50), 1f)
        )

        root.addView(searchContainer)

        // CONTENIDO DESPLAZABLE
        val scroll = ScrollView(this).apply {
            fillViewport = true
        }

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(24))
        }

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // NAVEGACIÓN
        val navigationScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            setBackgroundColor(Color.WHITE)
        }

        val navigation = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(dp(5), dp(5), dp(5), dp(5))
        }

        val sections = listOf(
            "Inicio",
            "Aura",
            "Emociones",
            "Síntomas",
            "Curso",
            "Perdón"
        )

        sections.forEach { section ->

            val button = Button(this).apply {
                text = section
                textSize = 11f
                isAllCaps = false
                setTextColor(navy)

                setOnClickListener {
                    search.setText("")
                    showSection(section)
                }
            }

            navigation.addView(
                button,
                LinearLayout.LayoutParams(
                    dp(92),
                    dp(48)
                )
            )
        }

        navigationScroll.addView(navigation)
        root.addView(navigationScroll)

        setContentView(root)

        // BÚSQUEDA EN TIEMPO REAL
        search.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val query = s?.toString()?.trim().orEmpty()

                if (query.isEmpty()) {
                    showSection(currentSection)
                } else {
                    showSearch(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        showSection("Inicio")
    }

    // CARGAR BIBLIOTECA LOCAL
    private fun loadLibrary() {
        try {
            val json = assets.open("library.json")
                .bufferedReader()
                .use { it.readText() }

            library = JSONObject(json)

        } catch (e: Exception) {
            library = JSONObject()
            loadError = true
        }
    }

    // CAMBIAR SECCIÓN
    private fun showSection(section: String) {

        currentSection = section
        content.removeAllViews()

        if (loadError) {
            heading("Biblioteca no disponible")
            paragraph(
                "No se ha podido cargar library.json. " +
                "Comprueba que esté dentro de app/src/main/assets."
            )
            return
        }

        when (section) {

            "Inicio" -> {
                heading("Bienvenido a Aura Quirochema")

                paragraph(
                    "Un espacio de consulta sobre aura, colores, " +
                    "emociones, chakras, interpretaciones simbólicas " +
                    "y Un Curso de Milagros."
                )

                heading("Explora la biblioteca")

                listOf(
                    "Aura",
                    "Emociones",
                    "Síntomas",
                    "Curso",
                    "Perdón"
                ).forEach { name ->

                    action(name) {
                        showSection(name)
                    }
                }

                heading("Buscador inteligente")

                paragraph(
                    "Escribe una palabra para localizar información " +
                    "en todos los apartados de la biblioteca."
                )

                paragraph(
                    "La información se consulta desde el contenido " +
                    "local instalado en tu dispositivo."
                )
            }

            "Aura" -> showArray(
                "colors",
                "Colores del aura"
            )

            "Emociones" -> showArray(
                "emotions",
                "Emociones"
            )

            "Síntomas" -> showArray(
                "symptoms",
                "Lecturas simbólicas"
            )

            "Curso" -> showArray(
                "ucdm",
                "Un Curso de Milagros"
            )

            "Perdón" -> showArray(
                "prayers",
                "Oraciones y reflexiones"
            )
        }
    }

    // MOSTRAR CONTENIDO JSON
    private fun showArray(key: String, title: String) {

        heading(title)

        val array = library.optJSONArray(key)

        if (array == null || array.length() == 0) {
            paragraph("Todavía no hay contenidos disponibles en esta sección.")
            return
        }

        for (i in 0 until array.length()) {

            val obj = array.optJSONObject(i) ?: continue

            val name = obj.optString(
                "name",
                obj.optString("title", "Contenido")
            )

            card(name, obj)
        }
    }

    // BUSCADOR GLOBAL
    private fun showSearch(query: String) {

        content.removeAllViews()

        heading("Resultados de búsqueda")

        paragraph("Buscando: $query")

        if (loadError) {
            paragraph("No se ha podido cargar la biblioteca.")
            return
        }

        val keys = listOf(
            "colors",
            "emotions",
            "symptoms",
            "ucdm",
            "prayers"
        )

        var found = 0

        for (key in keys) {

            val array = library.optJSONArray(key) ?: continue

            for (i in 0 until array.length()) {

                val obj = array.optJSONObject(i) ?: continue

                if (obj.toString().contains(query, ignoreCase = true)) {

                    val title = obj.optString(
                        "name",
                        obj.optString("title", "Resultado")
                    )

                    card(title, obj)
                    found++
                }
            }
        }

        if (found == 0) {
            paragraph(
                "No se encontraron coincidencias. " +
                "Prueba con otra palabra o un término relacionado."
            )
        } else {
            paragraph("Resultados encontrados: $found")
        }
    }

    // TARJETAS DE INFORMACIÓN
    private fun card(title: String, obj: JSONObject) {

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(17), dp(16), dp(17), dp(17))
            background = rounded(Color.WHITE, dp(14), Color.rgb(232, 236, 238))
        }

        val titleView = TextView(this).apply {
            text = title
            textSize = 18f
            setTextColor(teal)
            typeface = Typeface.DEFAULT_BOLD
        }

        box.addView(titleView)

        val fields = listOf(
            "meaning",
            "strengths",
            "challenges",
            "chakra",
            "description",
            "symbolic",
            "reflection",
            "question",
            "practice",
            "safety",
            "summary",
            "text",
            "family",
            "frequency",
            "origin",
            "source",
            "updated",
            "notes",
            "limitations",
            "indications",
            "contraindications",
            "references",
            "steps",
            "tags"
        )

        val labels = mapOf(
            "meaning" to "Significado",
            "strengths" to "Fortalezas",
            "challenges" to "Posibles retos",
            "chakra" to "Chakra relacionado",
            "description" to "Descripción",
            "symbolic" to "Interpretación simbólica",
            "reflection" to "Reflexión",
            "question" to "Pregunta para explorar",
            "practice" to "Práctica",
            "safety" to "Importante",
            "summary" to "Resumen",
            "text" to "Contenido",
            "family" to "Familia",
            "frequency" to "Frecuencia",
            "origin" to "Origen",
            "source" to "Fuente",
            "updated" to "Actualización",
            "notes" to "Notas",
            "limitations" to "Limitaciones",
            "indications" to "Indicaciones",
            "contraindications" to "Contraindicaciones",
            "references" to "Referencias",
            "steps" to "Pasos",
            "tags" to "Etiquetas"
        )

        fields.forEach { key ->

            val value = obj.opt(key)

            if (value != null && value != JSONObject.NULL) {

                val text = when (value) {
                    is JSONArray -> {
                        (0 until value.length()).joinToString(", ") {
                            value.optString(it)
                        }
                    }
                    is JSONObject -> value.toString(2)
                    else -> value.toString()
                }

                if (text.isNotBlank()) {

                    val fieldView = TextView(this).apply {
                        this.text = "${labels[key] ?: key}: $text"
                        textSize = 14f
                        setTextColor(ink)
                        setPadding(0, dp(10), 0, 0)
                        setTextIsSelectable(true)
                    }

                    box.addView(fieldView)
                }
            }
        }

        val margin = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        margin.bottomMargin = dp(13)

        content.addView(box, margin)
    }

    // TÍTULOS
    private fun heading(text: String) {

        val view = TextView(this).apply {
            this.text = text
            textSize = 21f
            setTextColor(ink)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(dp(3), dp(16), dp(3), dp(10))
        }

        content.addView(view)
    }

    // PÁRRAFOS
    private fun paragraph(text: String) {

        val view = TextView(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(ink)
            setPadding(dp(4), dp(7), dp(4), dp(10))
            setLineSpacing(dp(3).toFloat(), 1f)
        }

        content.addView(view)
    }

    // BOTONES
    private fun action(text: String, run: () -> Unit) {

        val button = Button(this).apply {
            this.text = text
            isAllCaps = false
            textSize = 14f
            setTextColor(Color.WHITE)
            background = rounded(teal, dp(12), teal)
            setOnClickListener { run() }
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(52)
        )

        params.bottomMargin = dp(9)

        content.addView(button, params)
    }

    // FONDOS REDONDEADOS
    private fun rounded(
        color: Int,
        radius: Int,
        stroke: Int
    ): GradientDrawable {

        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius.toFloat()

            if (stroke != 0) {
                setStroke(dp(1), stroke)
            }
        }
    }

    // CONVERSIÓN DE MEDIDAS
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
