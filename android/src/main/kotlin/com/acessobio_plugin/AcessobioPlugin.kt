package com.acessobio_plugin

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.lang.reflect.Field

/** AcessobioPlugin */
class AcessobioPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  private val REQUEST_BIO = 7
  private lateinit var channel : MethodChannel
  private lateinit var activity: Activity
  private lateinit var result: Result
  private lateinit var urlIntance : String
  private lateinit var apikey : String
  private lateinit var authToken : String

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "acessobio")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    this.result = result

    validKeys(call.argument("urlIntance"), call.argument("apikey"), call.argument("authToken"))

    when(call.method){

      //Liveness
      "openLiveness" -> acessoBioLiveness(call.method)
      "openLivenessWithCreateProcess" -> acessoBioLiveness(call.method,call.argument("name"),call.argument("document"))

      //Document
      "openCameraDocumentOCR" -> openCameraDocumentOCR(call.method,call.argument("DOCUMENT_TYPE"))
      "openFaceMatch" -> openCameraDocumentOCR(call.method,call.argument("DOCUMENT_TYPE"))
      "openCameraDocument" -> openCameraDocumentOCR(call.method,call.argument("DOCUMENT_TYPE"))
      "openCameraInsertDocument" -> openCameraInsertDocument(call.method,
              call.argument("code"),
              call.argument("nome"),
              call.argument("DOCUMENT_TYPE")
      )

      //Auth
      "openLivenessAuthenticate" -> openLivenessAuthenticate(call.method,call.argument("code"))

      //Camera
      "openCamera" -> openCamera(call.method)
      "openCameraWithCreateProcess" -> openCamera(call.method,
                      call.argument("nome"),
                      call.argument("code"),
                      call.argument("gender"),
                      call.argument("birthdate"),
                      call.argument("email"),
                      call.argument("phone")
      )

      else -> result.notImplemented()
    }

  }

  //region Liviness

  private fun acessoBioLiveness(methodCall: String) {

    activity.startActivityForResult(getIntent(AcessoBioLiveness(),methodCall), REQUEST_BIO)

  }

  private fun acessoBioLiveness(methodCall: String, name: String?, document: String?) {

    val intent = getIntent(AcessoBioLiveness(),methodCall)

    intent.putExtra("name", name)
    intent.putExtra("document", document)

    activity.startActivityForResult(intent, REQUEST_BIO)

  }

  //endregion

  //region Document

  private fun openCameraDocumentOCR(method: String, DOCUMENT_TYPE: Int?) {

    val intent = getIntent(AcessoBioDocument(),method)

    intent.putExtra("DOCUMENT_TYPE", DOCUMENT_TYPE)

    activity.startActivityForResult(intent, REQUEST_BIO)

  }

  private fun openCameraInsertDocument(method: String, code: String?, nome: String?, DOCUMENT_TYPE: Int?) {

    val intent = getIntent(AcessoBioCamera(),method)

    intent.putExtra("code", code)
    intent.putExtra("nome", nome)
    intent.putExtra("DOCUMENT_TYPE", DOCUMENT_TYPE)

    activity.startActivityForResult(intent, REQUEST_BIO)

  }

  //endregion

  //region Auth

  private fun openLivenessAuthenticate(methodCall: String, code: String?) {

    val intent = getIntent(AcessoBioAuthenticate(),methodCall)

    intent.putExtra("code", code)

    activity.startActivityForResult(intent, REQUEST_BIO)

  }

  //endregion

  //region Camera

  private fun openCamera(methodCall: String) {

    activity.startActivityForResult(getIntent(AcessoBioCamera(),methodCall), REQUEST_BIO)

  }

  private fun openCamera(method: String, nome: String?,code: String?, gender: String?, birthdate: String?, email: String?, phone: String?) {

    val intent = getIntent(AcessoBioCamera(),method)

    intent.putExtra("nome", nome)
    intent.putExtra("code", code)
    intent.putExtra("gender", gender)
    intent.putExtra("birthdate", birthdate)
    intent.putExtra("email", email)
    intent.putExtra("phone", phone)


    activity.startActivityForResult(intent, REQUEST_BIO)

  }

  //endregion

  private fun getIntent(acessoBio: AcessoBio, methodCall: String): Intent {

    acessoBio.setPluginContext(this)

    val intent = Intent(activity, acessoBio::class.java)

    intent.putExtra("urlIntance",urlIntance)
    intent.putExtra("apikey",apikey)
    intent.putExtra("authToken",authToken)
    intent.putExtra("methodCall",methodCall)

    return intent

  }
  private fun validKeys(urlIntance: String?, apikey: String?, authToken: String?) {
    if(urlIntance == null || apikey == null || authToken == null ) {
      this.result.success("Informe urlIntance, apikey, authToken para proceguir")
    }else{
      this.urlIntance = urlIntance
      this.apikey = apikey
      this.authToken = authToken
    }
  }

  //region RETURN THE RESULTS

  //SUCCESS
  fun onSuccessPlugin(resultBio: Any) {
    result.success(convertObjToMapReflection(resultBio,1))//Status false pq vem do onSuccess
  }

  fun onSuccessPlugin(resultBio: Boolean) {
    result.success(convertObjToMapReflection(resultBio,1))//Status false pq vem do onSuccess
  }

  fun onSuccessPlugin(resultBio: String) {
    result.success(convertObjToMapReflection(resultBio,1))//Status false pq vem do onSuccess
  }

  fun onSuccessPlugin(resultBio: Int) {
    result.success(convertObjToMapReflection(resultBio,1))//Status false pq vem do onSuccess
  }

  //ERRORS
  fun onErrorPlugin(error: Any) {
    result.success(convertObjToMapReflection(error, 0))//Status false pq vem do onError
  }

  fun onErrorPlugin(error: String) {
    result.success(convertObjToMapReflection(error, 0))//Status false pq vem do onError
  }

  fun onErrorPlugin(error: Int) {
    result.success(convertObjToMapReflection(error, 0))//Status false pq vem do onError
  }

  //ERROR ACESSOBIO
  fun onErrorPluginAcessoBio(error: Any) {
    result.success(convertObjToMapReflection(error, 0))//Status false pq vem do onError
  }

  fun userClosedCameraManually() {
    result.success(convertObjToMapReflection(0,-1))//Status false pq vem do onError
  }

  //endregion

  //region Convert to HashMap

  private fun convertObjToMapReflection(objects: Any, status: Int): java.util.HashMap<String, Any> {

    val hashMap:HashMap<String,Any> = HashMap()

    val allFields: Array<Field> = objects.javaClass.declaredFields

    for (field in allFields) {
      field.isAccessible = true

      val value = field[objects]

      if(value != null){
        hashMap[field.name] = value
      }else{
        hashMap[field.name] = ""
      }

    }

    hashMap["flutterstatus"] = status

    return hashMap

  }
  private fun convertObjToMapReflection(result: Boolean, status: Int): java.util.HashMap<String, Any> {

    val hashMap:HashMap<String,Any> = HashMap()

    hashMap["result"] = result
    hashMap["flutterstatus"] = status

    return hashMap

  }
  private fun convertObjToMapReflection(result: String, status: Int): java.util.HashMap<String, Any> {

    val hashMap:HashMap<String,Any> = HashMap()

    hashMap["result"] = result
    hashMap["flutterstatus"] = status

    return hashMap

  }
  private fun convertObjToMapReflection(result: Int, status: Int): java.util.HashMap<String, Any> {

    val hashMap:HashMap<String,Any> = HashMap()

    hashMap["result"] = result
    hashMap["flutterstatus"] = status

    return hashMap

  }

  //endregion

  //region ActivityAware
  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivity() {

  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {

  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }
  //endregion


}
