package com.example.formularioasistencia

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var btnSubmit: Button
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etInstitucion: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etDni: EditText
    private lateinit var etCodigo: EditText
    private lateinit var etCorreo: EditText
    private lateinit var edadSeekBar: SeekBar
    private lateinit var spinnerParticipante: Spinner
    private lateinit var tvEdad: TextView
    private lateinit var checkboxAcceptTerms: CheckBox
    private lateinit var menuOptions: ImageView
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupListeners()
        setupWebView()
    }

    private fun initializeViews() {
        btnSubmit = findViewById(R.id.btnSubmit)
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etInstitucion = findViewById(R.id.etInstitucion)
        etTelefono = findViewById(R.id.etTelefono)
        etDni = findViewById(R.id.etDni)
        etCodigo = findViewById(R.id.etCodigo)
        etCorreo = findViewById(R.id.etCorreo)
        edadSeekBar = findViewById(R.id.edadSeekBar)
        spinnerParticipante = findViewById(R.id.spinnerParticipante)
        tvEdad = findViewById(R.id.tvEdad)
        checkboxAcceptTerms = findViewById(R.id.checkboxAcceptTerms)
        menuOptions = findViewById(R.id.menu_options)
        webView = findViewById(R.id.webView)

        // Inicializar el botón como deshabilitado
        btnSubmit.isEnabled = false
    }

    private fun setupListeners() {
        // Configurar SeekBar para actualizar la edad
        edadSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvEdad.text = "$progress años"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configurar el listener del CheckBox para habilitar/deshabilitar el botón
        checkboxAcceptTerms.setOnCheckedChangeListener { _, isChecked ->
            btnSubmit.isEnabled = isChecked
        }

        // Acción para el botón de Confirmar Asistencia
        btnSubmit.setOnClickListener {
            handleSubmit()
        }

        // Configurar el menú de opciones
        menuOptions.setOnClickListener { view ->
            showOptionsMenu(view)
        }
    }

    private fun setupWebView() {
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Configurar el WebView para manejar el scroll correctamente
        webView.setOnTouchListener { view, event ->
            // Permitir que el WebView maneje el scroll cuando esté visible
            if (webView.visibility == View.VISIBLE) {
                view.parent.requestDisallowInterceptTouchEvent(true)
            }
            false
        }
    }

    private fun showOptionsMenu(view: View) {
        val popupMenu = PopupMenu(this, view)

        // Agregar las opciones del menú
        popupMenu.menu.add(0, 1, 0, "WebView")
        popupMenu.menu.add(0, 2, 0, "Info")

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                1 -> {
                    // Mostrar WebView
                    showWebView()
                    true
                }
                2 -> {
                    // Mostrar información
                    showInfoDialog()
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showWebView() {
        if (webView.visibility == View.GONE) {
            // Mostrar el WebView y cargar la página
            webView.visibility = View.VISIBLE
            webView.loadUrl("https://www.gob.pe/institucion/inabif/noticias/204485-car-san-pedrito-de-ancash-celebra-22-aniversario")

            // Hacer scroll para mostrar el WebView
            val scrollView = findViewById<android.widget.ScrollView>(R.id.main)
            scrollView.post {
                scrollView.smoothScrollTo(0, webView.top)
            }

            // Mostrar un mensaje informativo
            Toast.makeText(this, "Puedes navegar dentro del WebView. Usa el botón atrás para volver.", Toast.LENGTH_LONG).show()
        } else {
            // Ocultar el WebView
            webView.visibility = View.GONE
            Toast.makeText(this, "WebView ocultado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showInfoDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Información de la App")
        builder.setMessage("Aplicación para registro de participantes en el Desfile de San Pedrito - Edición Bicentenario\n\n" +
                "Versión: 1.0\n" +
                "Desarrollada para facilitar el registro y confirmación de asistencia al evento.")
        builder.setPositiveButton("Cerrar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun handleSubmit() {
        // Obtener los valores ingresados
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val institucion = etInstitucion.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val codigo = etCodigo.text.toString().trim()
        val correo = etCorreo.text.toString().trim()

        // Verificar si algún campo obligatorio está vacío
        if (nombre.isEmpty() || apellido.isEmpty() || institucion.isEmpty() ||
            telefono.isEmpty() || dni.isEmpty() || codigo.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validaciones adicionales
        if (!isValidEmail(correo)) {
            Toast.makeText(this, "Por favor ingrese un correo válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (dni.length != 8) {
            Toast.makeText(this, "El DNI debe tener 8 dígitos", Toast.LENGTH_SHORT).show()
            return
        }

        // Si todo está correcto, mostrar confirmación
        showConfirmationDialog()
    }

    private fun showConfirmationDialog() {
        // Crear el dialog personalizado
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_confirmation)

        // Configurar el dialog
        dialog.setCancelable(false) // No se puede cerrar tocando fuera
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Fondo transparente

        // Obtener el botón del dialog
        val btnRegisterAnother = dialog.findViewById<Button>(R.id.btnRegisterAnother)

        // Configurar el botón para cerrar el dialog y limpiar el formulario
        btnRegisterAnother.setOnClickListener {
            dialog.dismiss() // Cerrar el dialog
            resetForm() // Limpiar el formulario
        }

        // Mostrar el dialog
        dialog.show()
    }

    private fun resetForm() {
        // Limpiar todos los campos
        etNombre.text.clear()
        etApellido.text.clear()
        etInstitucion.text.clear()
        etTelefono.text.clear()
        etDni.text.clear()
        etCodigo.text.clear()
        etCorreo.text.clear()

        // Resetear la edad a 18
        edadSeekBar.progress = 18
        tvEdad.text = "18 años"

        // Resetear el spinner al primer elemento
        spinnerParticipante.setSelection(0)

        // Desmarcar el checkbox
        checkboxAcceptTerms.isChecked = false

        // Deshabilitar el botón submit
        btnSubmit.isEnabled = false

        // Ocultar el WebView si está visible
        webView.visibility = View.GONE

        // Enfocar el primer campo
        etNombre.requestFocus()

        // Hacer scroll hacia arriba del formulario
        val scrollView = findViewById<android.widget.ScrollView>(R.id.main)
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_UP)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onBackPressed() {
        // Si el WebView está visible y puede retroceder, hacer que retroceda
        if (webView.visibility == View.VISIBLE && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}