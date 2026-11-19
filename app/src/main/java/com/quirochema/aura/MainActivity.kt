package com.quirochema.aura

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : Activity() {
    private val ink = Color.rgb(35,43,52)
    private val teal = Color.rgb(53,157,166)
    private val paper = Color.rgb(247,249,250)
    private lateinit var content: LinearLayout
    private lateinit var search: EditText
    private lateinit var library: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        library = JSONObject(assets.open("library.json").bufferedReader().use { it.readText() })
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setBackgroundColor(paper) }
        val header = TextView(this).apply {
            text="AURA QUIROCHEMA\nBiblioteca holística"
            textSize=23f; setTextColor(Color.WHITE); gravity=Gravity.CENTER
            setPadding(18,28,18,24); setBackgroundColor(Color.rgb(23,34,57)); typeface=Typeface.DEFAULT_BOLD
        }
        root.addView(header)
        search = EditText(this).apply { hint="Buscar aura, emoción, síntoma o perdón…"; setSingleLine(true); setPadding(16,12,16,12) }
        root.addView(search)
        val scroll=ScrollView(this)
        content=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(14,8,14,24) }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1,0,1f))
        val tabs=LinearLayout(this).apply { orientation=LinearLayout.HORIZONTAL; setPadding(6,6,6,6); setBackgroundColor(Color.WHITE) }
        listOf("Inicio","Aura","Emociones","Síntomas","Curso","Perdón").forEach { label ->
            val b=Button(this).apply { text=label; textSize=10f; setOnClickListener { showSection(label) } }
            tabs.addView(b, LinearLayout.LayoutParams(0,-2,1f))
        }
        root.addView(tabs)
        setContentView(root)
        search.addTextChangedListener(object: android.text.TextWatcher {
            override fun beforeTextChanged(s:CharSequence?,start:Int,count:Int,after:Int){}
            override fun onTextChanged(s:CharSequence?,start:Int,before:Int,count:Int){ showSearch(s?.toString().orEmpty()) }
            override fun afterTextChanged(s:android.text.Editable?){}
        })
        showSection("Inicio")
    }

    private fun showSection(section:String) {
        content.removeAllViews()
        when(section) {
            "Inicio" -> {
                heading("Bienvenido a Aura Quirochema")
                paragraph("Una biblioteca de consulta sobre aura, emociones, interpretaciones simbólicas, Un Curso de Milagros y prácticas de perdón.")
                listOf("Aura","Emociones","Síntomas","Curso","Perdón").forEach { name ->
                    action(name) { showSection(name) }
                }
                paragraph("Usa el buscador para localizar cualquier concepto.")
            }
            "Aura" -> showArray("colors","Colores del aura")
            "Emociones" -> showArray("emotions","Emociones")
            "Síntomas" -> showArray("symptoms","Síntomas · lectura simbólica")
            "Curso" -> showArray("ucdm","Un Curso de Milagros · síntesis y reflexión")
            "Perdón" -> showArray("prayers","Oraciones y frases de perdón")
        }
    }
    private fun showArray(key:String,title:String) {
        heading(title)
        val arr=library.optJSONArray(key) ?: return
        for(i in 0 until arr.length()) {
            val obj=arr.getJSONObject(i)
            val name=obj.optString("name",obj.optString("title","Contenido"))
            card(name, obj)
        }
    }
    private fun showSearch(q:String) {
        if(q.isBlank()){ showSection("Inicio"); return }
        content.removeAllViews()
        heading("Resultados: $q")
        var found=0
        val keys=listOf("colors","emotions","symptoms","ucdm","prayers")
        for(key in keys) {
            val arr=library.optJSONArray(key) ?: continue
            for(i in 0 until arr.length()) {
                val obj=arr.getJSONObject(i)
                if(obj.toString().contains(q,true)) {
                    card(obj.optString("name",obj.optString("title","Resultado")),obj); found++
                }
            }
        }
        if(found==0) paragraph("No hay coincidencias en la biblioteca local. Prueba otra palabra o una variante.")
    }
    private fun card(title:String,obj:JSONObject) {
        val box=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(16,14,16,14); setBackgroundColor(Color.WHITE) }
        val h=TextView(this).apply { text=title; textSize=18f; setTextColor(teal); typeface=Typeface.DEFAULT_BOLD }
        box.addView(h)
        val fields=listOf("meaning","strengths","challenges","chakra","description","symbolic","reflection","question","practice","safety","summary","text")
        fields.forEach { key ->
            val value=obj.optString(key,"")
            if(value.isNotBlank()) {
                val label=when(key) {
                    "meaning"->"Significado"; "strengths"->"Fortalezas"; "challenges"->"Posibles retos"; "chakra"->"Chakra"; "description"->"Descripción"; "symbolic"->"Lectura simbólica"; "reflection"->"Reflexión"; "question"->"Pregunta"; "practice"->"Práctica"; "safety"->"Importante"; "summary"->"Síntesis"; else -> "Texto"
                }
                val t=TextView(this).apply { text="$label: $value"; textSize=14f; setTextColor(ink); setPadding(0,8,0,0) }
                box.addView(t)
            }
        }
        val lp=LinearLayout.LayoutParams(-1,-2); lp.bottomMargin=12
        content.addView(box,lp)
    }
    private fun heading(t:String) { content.addView(TextView(this).apply { text=t; textSize=21f; typeface=Typeface.DEFAULT_BOLD; setTextColor(ink); setPadding(4,14,4,12) }) }
    private fun paragraph(t:String) { content.addView(TextView(this).apply { text=t; textSize=15f; setTextColor(ink); setPadding(4,8,4,12) }) }
    private fun action(t:String,run:()->Unit) { content.addView(Button(this).apply { text=t; setOnClickListener { run() } }) }
}
