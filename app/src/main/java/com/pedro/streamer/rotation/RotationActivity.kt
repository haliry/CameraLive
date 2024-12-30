/*
 * Copyright (C) 2024 pedroSG94.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pedro.streamer.rotation

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.pedro.encoder.input.sources.audio.MicrophoneSource
import com.pedro.encoder.input.sources.video.Camera1Source
import com.pedro.encoder.input.sources.video.Camera2Source
import com.pedro.encoder.input.video.CameraHelper
import com.pedro.extrasources.BitmapSource
import com.pedro.extrasources.CameraUvcSource
import com.pedro.extrasources.CameraXSource
import com.pedro.streamer.R
import com.pedro.streamer.rotation.eventbus.BroadcastBackPressedEvent
import com.pedro.streamer.utils.FilterMenu
import com.pedro.streamer.utils.Logger
import com.pedro.streamer.utils.toast
import com.pedro.streamer.utils.updateMenuColor
import org.greenrobot.eventbus.EventBus


/**
 * Created by pedro on 22/3/22.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RotationActivity : AppCompatActivity(), OnTouchListener {
  companion object {
    private const val TAG = "RotationActivity"
    private const val EXIT_TIME_INTERVAL = 2000
  }

  private val cameraFragment = CameraFragment.getInstance()
  private val filterMenu: FilterMenu by lazy { FilterMenu(this) }
  private var currentVideoSource: MenuItem? = null
  private var currentAudioSource: MenuItem? = null
  private var currentOrientation: MenuItem? = null
  private var currentFilter: MenuItem? = null
  private var currentPlatform: MenuItem? = null
  private var mClickTime: Long = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.rotation_activity)
    supportFragmentManager.beginTransaction().add(R.id.container, cameraFragment).commit()

    initBackEventListener()
  }

  private fun initBackEventListener(){
    onBackPressedListener(true){
      EventBus.getDefault().post(BroadcastBackPressedEvent())
    }
  }

  fun handleBackEvent(){
    Logger.d(TAG, "handleBackEvent: ")
    if((System.currentTimeMillis() - mClickTime) > EXIT_TIME_INTERVAL){
      toast("Press again to exit app")
      mClickTime = System.currentTimeMillis()
    }else{
      finish()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.rotation_menu, menu)
    val defaultVideoSource = menu.findItem(R.id.video_source_camera2)
    val defaultAudioSource = menu.findItem(R.id.audio_source_microphone)
    val defaultOrientation = menu.findItem(R.id.orientation_horizontal)
    val defaultFilter = menu.findItem(R.id.no_filter)
    val defaultPlatform = menu.findItem(R.id.platform_youtube)
    currentVideoSource = defaultVideoSource.updateMenuColor(this, currentVideoSource)
    currentAudioSource = defaultAudioSource.updateMenuColor(this, currentAudioSource)
    currentOrientation = defaultOrientation.updateMenuColor(this, currentOrientation)
    currentFilter = defaultFilter.updateMenuColor(this, currentFilter)
    currentPlatform = defaultPlatform.updateMenuColor(this, currentPlatform)
    return true
  }

//  override fun onBackPressed() {
//    super.onBackPressed()
//    Logger.d(TAG, "onBackPressed: ")
//  }

//  override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//    if(keyCode == KeyEvent.KEYCODE_BACK){
//      Logger.d(TAG, "onKeyDown: ")
//
//    }
//    return super.onKeyDown(keyCode, event)
//  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    try {
      when (item.itemId) {
        R.id.video_source_camera1 -> {
          currentVideoSource = item.updateMenuColor(this, currentVideoSource)
          cameraFragment.genericStream.changeVideoSource(Camera1Source(applicationContext))
        }
        R.id.video_source_camera2 -> {
          currentVideoSource = item.updateMenuColor(this, currentVideoSource)
          cameraFragment.genericStream.changeVideoSource(Camera2Source(applicationContext))
        }
        R.id.video_source_camerax -> {
          currentVideoSource = item.updateMenuColor(this, currentVideoSource)
          cameraFragment.genericStream.changeVideoSource(CameraXSource(applicationContext))
        }
        R.id.video_source_camera_uvc -> {
          currentVideoSource = item.updateMenuColor(this, currentVideoSource)
          cameraFragment.genericStream.changeVideoSource(CameraUvcSource())
        }
        R.id.video_source_bitmap -> {
          currentVideoSource = item.updateMenuColor(this, currentVideoSource)
          val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
          cameraFragment.genericStream.changeVideoSource(BitmapSource(bitmap))
        }
        R.id.audio_source_microphone -> {
          currentAudioSource = item.updateMenuColor(this, currentAudioSource)
          cameraFragment.genericStream.changeAudioSource(MicrophoneSource())
        }
        R.id.orientation_horizontal -> {
          currentOrientation = item.updateMenuColor(this, currentOrientation)
          cameraFragment.setOrientationMode(false)
        }
        R.id.orientation_vertical -> {
          currentOrientation = item.updateMenuColor(this, currentOrientation)
          cameraFragment.setOrientationMode(true)
        }
        R.id.platform_huya -> {
          currentPlatform = item.updateMenuColor(this, currentPlatform)
          cameraFragment.streamUrl.setText(R.string.stream_url_huya)
        }
        R.id.platform_tiktok -> {
          currentPlatform = item.updateMenuColor(this, currentPlatform)
          cameraFragment.streamUrl.setText(R.string.stream_url_tiktok)
        }
        R.id.platform_youtube -> {
          currentPlatform = item.updateMenuColor(this, currentPlatform)
          cameraFragment.streamUrl.setText(R.string.stream_url_youtube)
        }
        else -> {
          val result = filterMenu.onOptionsItemSelected(item, cameraFragment.genericStream.getGlInterface())
          if (result) currentFilter = item.updateMenuColor(this, currentFilter)
          return result
        }
      }
    } catch (e: IllegalArgumentException) {
      toast("Change source error: ${e.message}")
    }
    return super.onOptionsItemSelected(item)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
    if (filterMenu.spriteGestureController.spriteTouched(view, motionEvent)) {
      filterMenu.spriteGestureController.moveSprite(view, motionEvent)
      filterMenu.spriteGestureController.scaleSprite(motionEvent)
      return true
    }
    return false
  }
}

fun AppCompatActivity.onBackPressedListener(isEnabled: Boolean, callback: () -> Unit){
  onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(isEnabled){
    override fun handleOnBackPressed() {
      callback()
    }
  })
}