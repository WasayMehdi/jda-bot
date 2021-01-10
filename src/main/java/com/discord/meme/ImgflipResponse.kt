package com.discord.meme

data class ImgflipResponse(val success: Boolean, val data: ImgflipUrls)

data class ImgflipUrls(val url: String, val page_url: String)

