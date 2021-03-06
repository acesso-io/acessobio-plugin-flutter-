package com.acessobio_plugin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.acesso.acessobio_android.AcessoBio
import com.acesso.acessobio_android.iAcessoBio
import com.acesso.acessobio_android.services.dto.ErrorBio

abstract class AcessoBio : AppCompatActivity(), iAcessoBio {

    companion object {
        var methodCall : String? = null
        var pluginContext: AcessobioPlugin? = null
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val REQUEST_CODE_PERMISSIONS = 10
    }

    lateinit var acessoBio: AcessoBio

    abstract fun callMethodBio()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        methodCall = intent.getStringExtra("methodCall")

        initAcessoBio(
                intent.getStringExtra("urlIntance"),
                intent.getStringExtra("apikey"),
                intent.getStringExtra("authToken")
        )

        //acessoBio.setColorBackground()

        if (!getPermission()) {
            getPermission()
        } else {
            callMethodBio()
        }

    }

    fun setPluginContext(context: AcessobioPlugin) {
        pluginContext = context
    }

    //ACESSOBIO
    private fun initAcessoBio(urlIntance: String?, apikey: String?, authToken: String?) {

        if (urlIntance != null && apikey != null && authToken != null) {
            createAcessoBio(
                    urlIntance,
                    apikey,
                    authToken
            )
        } else onError("informe urlIntance, apikey, authToken para proceguir.")

    }

    private fun createAcessoBio(urlIntance: String, apikey: String, authToken: String) {
        acessoBio = AcessoBio(
                this,
                urlIntance,
                apikey,
                authToken
        )
    }

    //SUCCESS
    protected fun onSuccess(result: Any?){
        if(pluginContext != null){
            if(result != null){
                pluginContext!!.onSuccessPlugin(result)
            }else{
                onError("erro desconhecido")
            }
        }else{
            onError("Erro ao retornar resultado, o contexto foi perdido")
        }
        finish()
    }

    protected fun onSuccess(result: Boolean){
        if(pluginContext != null){
            pluginContext!!.onSuccessPlugin(result)
        }else{
            onError("Erro ao retornar resultado, o contexto foi perdido")
        }
        finish()
    }

    protected fun onSuccess(result: String?){
        if(pluginContext != null){
            if(result != null || result != ""){
                pluginContext!!.onSuccessPlugin(result!!)
            }else{
                onError("Erro ao retornar resultado")
            }
        }else{
            onError("Erro ao retornar resultado, o contexto foi perdido")
        }
        finish()
    }

    protected fun onSuccess(result: Int?){
        if(pluginContext != null){
            if(result == null || result == 0){
                pluginContext!!.onSuccessPlugin(result!!)
            }else{
                onError("Erro ao retornar resultado")
            }
        }else{
            onError("Erro ao retornar resultado, o contexto foi perdido")
        }
        finish()
    }

    //ERROR
    protected fun onError(result: String) {

        if (pluginContext != null) {
            pluginContext!!.onErrorPlugin(result)
        }

        finish()
    }

    protected fun onError(result: Any?){
        if(pluginContext != null){
            if(result != null){
                pluginContext!!.onErrorPlugin(result)
            }else{
                onError("erro desconhecido")
            }
        }else{
            onError("Erro ao retornar resultado, o contexto foi perdido")
        }
        finish()
    }

    //ERROR AcessoBio
    override fun onErrorAcessoBio(errorBio: ErrorBio?) {
        if(pluginContext != null){
            if(errorBio != null){
                pluginContext!!.onErrorPluginAcessoBio(errorBio)
            }else{
                onError("erro desconhecido")
            }
        }else{
            onError("Erro ao retornar resultado, o contexto foi perdido")
        }
        finish()
    }

    override fun userClosedCameraManually() {
        pluginContext!!.userClosedCameraManually()
        finish()
    }

    //region Camera Permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != REQUEST_CODE_PERMISSIONS) {
            Toast.makeText(this, "Permissão acesso camera negada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPermission() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (!getPermission()) {
            requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    10
            )
        }
    }

    //endregion

}
