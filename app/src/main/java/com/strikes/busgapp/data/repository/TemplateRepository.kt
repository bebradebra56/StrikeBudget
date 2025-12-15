package com.strikes.busgapp.data.repository

import com.strikes.busgapp.data.dao.TemplateDao
import com.strikes.busgapp.data.entity.Template
import kotlinx.coroutines.flow.Flow

class TemplateRepository(private val templateDao: TemplateDao) {
    fun getAllTemplates(): Flow<List<Template>> = templateDao.getAllTemplates()

    suspend fun getTemplateById(id: Long): Template? = templateDao.getTemplateById(id)

    suspend fun insertTemplate(template: Template): Long = templateDao.insertTemplate(template)

    suspend fun updateTemplate(template: Template) = templateDao.updateTemplate(template)

    suspend fun deleteTemplate(template: Template) = templateDao.deleteTemplate(template)
}

