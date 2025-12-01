package com.example.ecomarketapk.utils
import com.example.ecomarketapk.data.ProductoResponse
import java.util.Locale

object SaludUtils{
    private fun normalizar(texto: String): String {
        return texto
            .lowercase(Locale.getDefault())
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
    }

    fun calcularSaludRating(producto: ProductoResponse): Int {
        val nombreRaw = producto.nombre ?: ""
        val categoriaRaw = producto.categoria ?: ""

        val nombre = normalizar(nombreRaw)
        val categoria = normalizar(categoriaRaw)

        val esZeroOLight = nombre.contains("zero") ||
                nombre.contains("light") ||
                nombre.contains("sin azucar") ||
                nombre.contains("sin azúcar") ||
                nombre.contains("baja en azucar") ||
                nombre.contains("diet")

        val esCola =
            nombre.contains("coca") ||
                    nombre.contains("cola") ||
                    nombre.contains("gaseosa") ||
                    nombre.contains("soda")

        val esEnergetica =
            nombre.contains("energetica") ||
                    nombre.contains("energy")

        if (esCola || esEnergetica) {
            return if (esZeroOLight) 2 else 1
        }

        if (nombre.contains("agua")) {

            val esSaborizada = nombre.contains("sabor") || nombre.contains("saborizada")
            val esConGas = nombre.contains(" con gas") ||
                    nombre.endsWith("con gas") ||
                    nombre.contains("agua con gas") ||
                    nombre.contains("gasificada") ||
                    nombre.contains("gasificada")

            val mencionaAzucar = nombre.contains("azucar") || nombre.contains("azúcar")
            if (!esSaborizada && !esConGas && !mencionaAzucar) {
                return 5
            }
            return if (esZeroOLight && !mencionaAzucar) 4 else 3
        }

        if (nombre.contains("jugo")) {
            val esNectar = nombre.contains("nectar")
            val es100 = nombre.contains("100%") || nombre.contains("100 por ciento")
            val esNatural = nombre.contains("natural")
            if (esNectar) return if (esZeroOLight) 3 else 2
            if (es100 || esNatural) return 4
            return if (esZeroOLight) 3 else 2
        }

        val esYogurt = nombre.contains("yogurt") || nombre.contains("yoghurt") || nombre.contains("yogur")
        val esLeche = nombre.contains("leche")

        if (esYogurt || esLeche) {
            if (esZeroOLight ||
                nombre.contains("descremada") ||
                nombre.contains("semi descremada") ||
                nombre.contains("semidescremada")
            ) {
                return 4
            }
            return 3
        }
        val esPan = nombre.contains("pan") || nombre.contains("marraqueta") || nombre.contains("hallulla")
        if (esPan) {
            return if (nombre.contains("integral") || nombre.contains("centeno")) 4 else 2
        }
        val esFrutoSeco = nombre.contains("almendra") ||
                nombre.contains("nuez") ||
                nombre.contains("mani") ||
                nombre.contains("pistacho") ||
                nombre.contains("castana")

        if (esFrutoSeco) {
            val esSalado = nombre.contains("sal") || nombre.contains("salado")
            return if (esSalado) 4 else 5
        }

        val esDulce = nombre.contains("chocolate") ||
                nombre.contains("galleta") ||
                nombre.contains("galletas") ||
                nombre.contains("dulce") ||
                nombre.contains("caramelo") ||
                nombre.contains("alfajor") ||
                nombre.contains("barquillo") ||
                nombre.contains("bombon") ||
                nombre.contains("snack") ||
                nombre.contains("papas fritas") ||
                nombre.contains("papasfritas") ||
                nombre.contains("ramitas")

        if (esDulce) {
            return if (esZeroOLight) 2 else 1
        }
        val ratingPorCategoria = when {
            categoria.contains("frutas") ||
                    categoria.contains("verduras") ||
                    categoria.contains("vegetales") -> 5

            categoria.contains("pescados") -> 4

            categoria.contains("carnes") -> 3

            categoria.contains("bebidas") -> 2

            categoria.contains("lacteos") ||
                    categoria.contains("lácteos") -> 3

            categoria.contains("dulces") ||
                    categoria.contains("snack") ||
                    categoria.contains("galletas") -> 1

            else -> 3
        }

        return ratingPorCategoria
    }

    fun textoNivelSalud(rating: Int): String {
        return when (rating) {
            5 -> "Muy saludable"
            4 -> "Saludable"
            3 -> "Moderado"
            2 -> "Poco saludable"
            1 -> "Muy poco saludable"
            else -> "Desconocido"
        }
    }

}