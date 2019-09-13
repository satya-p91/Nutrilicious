package online.forgottenbit.nutrilicious.view.common

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import kotlin.reflect.KClass

fun <T : ViewModel> FragmentActivity.getViewModel(modelClass: KClass<T>): T {
    return ViewModelProviders.of(this).get(modelClass.java)
}

fun <T : ViewModel> Fragment.getViewModel(modelClass: KClass<T>): T {
    return ViewModelProviders.of(this).get(modelClass.java)
}

fun AppCompatActivity.replaceFragment(viewGroupId: Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction()
            .replace(viewGroupId, fragment)
            .commit()
}

fun AppCompatActivity.addFragmentToState(@IdRes containerViewId: Int, fragment: Fragment, tag: String) {
    supportFragmentManager.beginTransaction().add(containerViewId, fragment, tag).commit()
}