package com.example.food.model

data class ModelProduct(private var name: String , private var isChecked: Boolean = false){

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getChecked(): Boolean {
        return isChecked
    }

    fun setChecked(checked: Boolean) {
        isChecked = checked
    }
}
