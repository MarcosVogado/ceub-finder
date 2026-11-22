package com.example.ceubfinder.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ceubfinder.data.model.Categoria
import com.example.ceubfinder.data.model.Objeto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CeubDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ceub_finder.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_CATEGORIAS = "categorias"
        const val TABLE_OBJETOS = "objetos"

        const val COL_CATEGORIA_ID = "id"
        const val COL_CATEGORIA_NOME = "nome"

        const val COL_OBJETO_ID = "id"
        const val COL_OBJETO_CATEGORIA_ID = "categoria_id"
        const val COL_OBJETO_NOME = "nome"
        const val COL_OBJETO_DESCRICAO = "descricao"
        const val COL_OBJETO_LOCAL_ESPECIFICO = "local_especifico"
        const val COL_OBJETO_PONTO_REFERENCIA = "ponto_referencia"
        const val COL_OBJETO_DETALHES_LOCAL = "detalhes_local"
        const val COL_OBJETO_DATA_ENCONTRADO = "data_encontrado"
        const val COL_OBJETO_DATA_CADASTRO = "data_cadastro"
        const val COL_OBJETO_FOTO_PATH = "foto_path"
        const val COL_OBJETO_STATUS = "status"
        const val COL_OBJETO_DATA_DEVOLUCAO = "data_devolucao"

        const val STATUS_DISPONIVEL = "DISPONIVEL"
        const val STATUS_DEVOLVIDO = "DEVOLVIDO"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys = ON;")

        val createCategoriasTable = """
            CREATE TABLE $TABLE_CATEGORIAS (
                $COL_CATEGORIA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CATEGORIA_NOME TEXT NOT NULL UNIQUE
            )
        """.trimIndent()

        val createObjetosTable = """
            CREATE TABLE $TABLE_OBJETOS (
                $COL_OBJETO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_OBJETO_CATEGORIA_ID INTEGER NOT NULL,
                $COL_OBJETO_NOME TEXT NOT NULL,
                $COL_OBJETO_DESCRICAO TEXT,
                $COL_OBJETO_LOCAL_ESPECIFICO TEXT NOT NULL,
                $COL_OBJETO_PONTO_REFERENCIA TEXT,
                $COL_OBJETO_DETALHES_LOCAL TEXT,
                $COL_OBJETO_DATA_ENCONTRADO TEXT NOT NULL,
                $COL_OBJETO_DATA_CADASTRO TEXT NOT NULL,
                $COL_OBJETO_FOTO_PATH TEXT,
                $COL_OBJETO_STATUS TEXT NOT NULL DEFAULT '$STATUS_DISPONIVEL',
                $COL_OBJETO_DATA_DEVOLUCAO TEXT,
                FOREIGN KEY($COL_OBJETO_CATEGORIA_ID) REFERENCES $TABLE_CATEGORIAS($COL_CATEGORIA_ID)
            )
        """.trimIndent()

        db.execSQL(createCategoriasTable)
        db.execSQL(createObjetosTable)

        seedInitialCategories(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Safe upgrade strategy: preserve data across versions without dropping tables
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys = ON;")
    }

    private fun seedInitialCategories(db: SQLiteDatabase) {
        val categoriasIniciais = listOf(
            "Eletrônicos",
            "Carteiras e acessórios",
            "Chaves",
            "Materiais acadêmicos",
            "Vestuário",
            "Garrafas",
            "Outros"
        )
        for (cat in categoriasIniciais) {
            val values = ContentValues().apply {
                put(COL_CATEGORIA_NOME, cat)
            }
            db.insert(TABLE_CATEGORIAS, null, values)
        }
    }

    fun getAllCategorias(): List<Categoria> {
        val lista = mutableListOf<Categoria>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CATEGORIAS ORDER BY $COL_CATEGORIA_NOME ASC", null)
        cursor.use {
            val idIdx = it.getColumnIndex(COL_CATEGORIA_ID)
            val nomeIdx = it.getColumnIndex(COL_CATEGORIA_NOME)
            while (it.moveToNext()) {
                lista.add(
                    Categoria(
                        id = it.getLong(idIdx),
                        nome = it.getString(nomeIdx)
                    )
                )
            }
        }
        return lista
    }

    fun insertObjeto(
        categoriaId: Long,
        nome: String,
        descricao: String?,
        localEspecifico: String,
        pontoReferencia: String?,
        detalhesLocal: String?,
        dataEncontrado: String,
        fotoPath: String?
    ): Long {
        val db = writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dataCadastro = dateFormat.format(Date())

        val values = ContentValues().apply {
            put(COL_OBJETO_CATEGORIA_ID, categoriaId)
            put(COL_OBJETO_NOME, nome)
            put(COL_OBJETO_DESCRICAO, descricao)
            put(COL_OBJETO_LOCAL_ESPECIFICO, localEspecifico)
            put(COL_OBJETO_PONTO_REFERENCIA, pontoReferencia)
            put(COL_OBJETO_DETALHES_LOCAL, detalhesLocal)
            put(COL_OBJETO_DATA_ENCONTRADO, dataEncontrado)
            put(COL_OBJETO_DATA_CADASTRO, dataCadastro)
            put(COL_OBJETO_FOTO_PATH, fotoPath)
            put(COL_OBJETO_STATUS, STATUS_DISPONIVEL)
        }
        return db.insert(TABLE_OBJETOS, null, values)
    }

    fun updateObjeto(
        id: Long,
        categoriaId: Long,
        nome: String,
        descricao: String?,
        localEspecifico: String,
        pontoReferencia: String?,
        detalhesLocal: String?,
        dataEncontrado: String,
        fotoPath: String?
    ): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_OBJETO_CATEGORIA_ID, categoriaId)
            put(COL_OBJETO_NOME, nome)
            put(COL_OBJETO_DESCRICAO, descricao)
            put(COL_OBJETO_LOCAL_ESPECIFICO, localEspecifico)
            put(COL_OBJETO_PONTO_REFERENCIA, pontoReferencia)
            put(COL_OBJETO_DETALHES_LOCAL, detalhesLocal)
            put(COL_OBJETO_DATA_ENCONTRADO, dataEncontrado)
            put(COL_OBJETO_FOTO_PATH, fotoPath)
        }
        return db.update(TABLE_OBJETOS, values, "$COL_OBJETO_ID = ?", arrayOf(id.toString()))
    }

    fun marcarComoDevolvido(id: Long): Int {
        val db = writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dataDevolucao = dateFormat.format(Date())

        val values = ContentValues().apply {
            put(COL_OBJETO_STATUS, STATUS_DEVOLVIDO)
            put(COL_OBJETO_DATA_DEVOLUCAO, dataDevolucao)
        }
        return db.update(TABLE_OBJETOS, values, "$COL_OBJETO_ID = ?", arrayOf(id.toString()))
    }

    fun excluirObjeto(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_OBJETOS, "$COL_OBJETO_ID = ?", arrayOf(id.toString()))
    }

    /**
     * INNER JOIN obrigatório para listagem pública de objetos disponíveis
     */
    fun getObjetosDisponiveis(querySearch: String? = null, categoriaId: Long? = null, localFilter: String? = null): List<Objeto> {
        val lista = mutableListOf<Objeto>()
        val db = readableDatabase

        var sql = """
            SELECT o.*, c.$COL_CATEGORIA_NOME AS categoria_nome
            FROM $TABLE_OBJETOS o
            INNER JOIN $TABLE_CATEGORIAS c ON c.$COL_CATEGORIA_ID = o.$COL_OBJETO_CATEGORIA_ID
            WHERE o.$COL_OBJETO_STATUS = '$STATUS_DISPONIVEL'
        """.trimIndent()

        val selectionArgs = mutableListOf<String>()

        if (!querySearch.isNullOrBlank()) {
            sql += " AND (o.$COL_OBJETO_NOME LIKE ? OR o.$COL_OBJETO_DESCRICAO LIKE ?)"
            selectionArgs.add("%$querySearch%")
            selectionArgs.add("%$querySearch%")
        }

        if (categoriaId != null && categoriaId > 0) {
            sql += " AND o.$COL_OBJETO_CATEGORIA_ID = ?"
            selectionArgs.add(categoriaId.toString())
        }

        if (!localFilter.isNullOrBlank()) {
            sql += " AND o.$COL_OBJETO_LOCAL_ESPECIFICO LIKE ?"
            selectionArgs.add("%$localFilter%")
        }

        sql += " ORDER BY o.$COL_OBJETO_DATA_CADASTRO DESC"

        val cursor = db.rawQuery(sql, selectionArgs.toTypedArray())
        cursor.use {
            while (it.moveToNext()) {
                lista.add(cursorToObjeto(it))
            }
        }
        return lista
    }

    fun getAllObjetosAdmin(statusFilter: String? = null): List<Objeto> {
        val lista = mutableListOf<Objeto>()
        val db = readableDatabase

        var sql = """
            SELECT o.*, c.$COL_CATEGORIA_NOME AS categoria_nome
            FROM $TABLE_OBJETOS o
            INNER JOIN $TABLE_CATEGORIAS c ON c.$COL_CATEGORIA_ID = o.$COL_OBJETO_CATEGORIA_ID
        """.trimIndent()

        val selectionArgs = mutableListOf<String>()
        if (!statusFilter.isNullOrBlank()) {
            sql += " WHERE o.$COL_OBJETO_STATUS = ?"
            selectionArgs.add(statusFilter)
        }

        sql += " ORDER BY o.$COL_OBJETO_DATA_CADASTRO DESC"

        val cursor = db.rawQuery(sql, selectionArgs.toTypedArray())
        cursor.use {
            while (it.moveToNext()) {
                lista.add(cursorToObjeto(it))
            }
        }
        return lista
    }

    fun getObjetoById(id: Long): Objeto? {
        val db = readableDatabase
        val sql = """
            SELECT o.*, c.$COL_CATEGORIA_NOME AS categoria_nome
            FROM $TABLE_OBJETOS o
            INNER JOIN $TABLE_CATEGORIAS c ON c.$COL_CATEGORIA_ID = o.$COL_OBJETO_CATEGORIA_ID
            WHERE o.$COL_OBJETO_ID = ?
        """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(id.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToObjeto(it)
            }
        }
        return null
    }

    private fun cursorToObjeto(cursor: android.database.Cursor): Objeto {
        return Objeto(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_OBJETO_ID)),
            categoriaId = cursor.getLong(cursor.getColumnIndexOrThrow(COL_OBJETO_CATEGORIA_ID)),
            categoriaNome = cursor.getString(cursor.getColumnIndexOrThrow("categoria_nome")),
            nome = cursor.getString(cursor.getColumnIndexOrThrow(COL_OBJETO_NOME)),
            descricao = cursor.getString(cursor.getColumnIndexOrThrow(COL_OBJETO_DESCRICAO)),
            localEspecifico = cursor.getString(cursor.getColumnIndexOrThrow(COL_OBJETO_LOCAL_ESPECIFICO)),
            pontoReferencia = cursor.getString(cursor.getColumnIndexOrThrow(COL_OBJETO_PONTO_REFERENCIA)),
            detalhesLocal = cursor.getString(cursor.getColumnIndexOrThrow(COL_OBJETO_DETALHES_LOCAL)),
            dataEncontrado = cursor.getString(cursor.getColumnIndexOrThrow(COL_OBJETO_DATA_ENCONTRADO)),
            dataCadastro = cursor.getString(cursor.getColumnIndexOrThrow(COL_OBJETO_DATA_CADASTRO)),
            fotoPath = cursor.getString(cursor.getColumnIndexOrThrow(COL_OBJETO_FOTO_PATH)),
            status = cursor.getString(cursor.getColumnIndexOrThrow(COL_OBJETO_STATUS)),
            dataDevolucao = cursor.getString(cursor.getColumnIndexOrThrow(COL_OBJETO_DATA_DEVOLUCAO))
        )
    }
}
