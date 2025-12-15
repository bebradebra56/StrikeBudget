package com.strikes.busgapp.data.repository

import com.strikes.busgapp.data.dao.WalletDao
import com.strikes.busgapp.data.entity.Wallet
import kotlinx.coroutines.flow.Flow

class WalletRepository(private val walletDao: WalletDao) {
    fun getAllWallets(): Flow<List<Wallet>> = walletDao.getAllWallets()

    suspend fun getWalletById(id: Long): Wallet? = walletDao.getWalletById(id)

    suspend fun getDefaultWallet(): Wallet? = walletDao.getDefaultWallet()

    suspend fun insertWallet(wallet: Wallet): Long = walletDao.insertWallet(wallet)

    suspend fun updateWallet(wallet: Wallet) = walletDao.updateWallet(wallet)

    suspend fun deleteWallet(wallet: Wallet) = walletDao.deleteWallet(wallet)
}

