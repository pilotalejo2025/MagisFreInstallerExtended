
package com.ale.magisfreeinstaller

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ale.magisfreeinstaller.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apps de TV y VOD
        binding.btnPluto.setOnClickListener { openPlay("tv.pluto.android") }
        binding.btnVix.setOnClickListener { openPlay("com.univision.vix") }
        binding.btnPlex.setOnClickListener { openPlay("plex") }
        binding.btnCanela.setOnClickListener { openPlay("com.canela.tv") }
        binding.btnDistro.setOnClickListener { openPlay("tv.distrotv") }

        // Películas y Anime
        binding.btnTubi.setOnClickListener { openPlay("com.tubitv") }
        binding.btnCrunchy.setOnClickListener { openPlay("com.crunchyroll.crunchyroid") }
        binding.btnRetroCrush.setOnClickListener { openPlay("com.digitalmediari.retrocrush") }

        // IPTV apps
        binding.btnOttNav.setOnClickListener { openPlay("studio.scillarium.ottnavigator") }
        binding.btnXciptv.setOnClickListener { openPlay("com.nathnetwork.xciptv") }
        binding.btnTivimate.setOnClickListener { openUrl("https://www.apkmirror.com/apk/aracde/tivimate/tivimate-android-tv-release/") }

        // Export lists (incluye movies y anime)
        binding.btnExportLists.setOnClickListener {
            exportLists(listOf("index.m3u","spa.m3u","sports.m3u","movies.m3u","anime.m3u"))
        }

        // Tips de subtítulos
        binding.btnSubsTips.setOnClickListener { showSubsTips() }
    }

    private fun showSubsTips() {
        val msg = " - En TiviMate/OTT Navigator: durante la reproducción, abre el menú -> 'Subtítulos/CC' y selecciona el track si el canal lo ofrece.\n" +
                  " - En apps VOD (Tubi, Plex, Crunchyroll, RetroCrush): usa el icono de subtítulos (CC) y elige ES/EN si está disponible.\n" +
                  " - Si no aparecen, el stream no incluye subtítulos embebidos."
        AlertDialog.Builder(this)
            .setTitle("Subtítulos (CC)")
            .setMessage(msg)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun exportLists(files: List<String>) {
        try {
            files.forEach { name ->
                assets.open(name).use { input ->
                    val out = File(getExternalFilesDir(null), name)
                    out.outputStream().use { output -> input.copyTo(output) }
                }
            }
            Toast.makeText(this, "Listas copiadas en Archivos de la app.", Toast.LENGTH_SHORT).show()
            openFile(getExternalFilesDir(null)?.absolutePath + "/spa.m3u")
        } catch (e: Exception) {
            Toast.makeText(this, "Error exportando: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun openPlay(pkg: String) {
        val uri = Uri.parse("market://details?id=$pkg")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try { startActivity(goToMarket) }
        catch (e: ActivityNotFoundException) {
            openUrl("https://play.google.com/store/apps/details?id=$pkg")
        }
    }

    private fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(i)
    }

    private fun openFile(path: String?) {
        path ?: return
        val file = java.io.File(path)
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/x-mpegURL")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            startActivity(Intent.createChooser(intent, "Abrir lista M3U con…"))
        } catch (e: Exception) {
            Toast.makeText(this, "Instala una app IPTV (TiviMate, OTT Navigator, XCIPTV).", Toast.LENGTH_LONG).show()
        }
    }
}
