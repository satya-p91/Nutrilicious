package online.forgottenbit.nutrilicious.view.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import kotlin.reflect.KClass

fun <T : ViewModel> AppCompatActivity.getViewModel(modelClass: KClass<T>): T {
    return ViewModelProviders.of(this).get(modelClass.java)
}

fun <T : ViewModel> androidx.fragment.app.Fragment.getViewModel(modelClass: KClass<T>): T {
    return ViewModelProviders.of(this).get(modelClass.java)
}

fun AppCompatActivity.replaceFragment(viewGroupId: Int, fragment: androidx.fragment.app.Fragment) {
    supportFragmentManager.beginTransaction()
            .replace(viewGroupId, fragment)
            .commit()
}

fun AppCompatActivity.addFragmentToState(@IdRes containerViewId: Int, fragment: androidx.fragment.app.Fragment, tag: String) {
    supportFragmentManager.beginTransaction().add(containerViewId, fragment, tag).commit()
}