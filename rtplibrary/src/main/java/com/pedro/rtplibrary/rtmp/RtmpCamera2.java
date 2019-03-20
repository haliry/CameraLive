package com.pedro.rtplibrary.rtmp;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.SurfaceView;
import android.view.TextureView;

import com.pedro.rtplibrary.base.Camera2Base;

import com.pedro.rtplibrary.view.LightOpenGlView;
import com.pedro.rtplibrary.view.OpenGlView;
import net.ossrs.rtmp.ConnectCheckerRtmp;
import net.ossrs.rtmp.SrsFlvMuxer;

import java.nio.ByteBuffer;

/**
 * More documentation see:
 * {@link com.pedro.rtplibrary.base.Camera2Base}
 *
 * Created by pedro on 6/07/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RtmpCamera2 extends Camera2Base {

  private SrsFlvMuxer srsFlvMuxer;

  public RtmpCamera2(SurfaceView surfaceView, ConnectCheckerRtmp connectChecker) {
    super(surfaceView);
    srsFlvMuxer = new SrsFlvMuxer(connectChecker);
  }

  public RtmpCamera2(TextureView textureView, ConnectCheckerRtmp connectChecker) {
    super(textureView);
    srsFlvMuxer = new SrsFlvMuxer(connectChecker);
  }

  public RtmpCamera2(OpenGlView openGlView, ConnectCheckerRtmp connectChecker) {
    super(openGlView);
    srsFlvMuxer = new SrsFlvMuxer(connectChecker);
  }

  public RtmpCamera2(LightOpenGlView lightOpenGlView, ConnectCheckerRtmp connectChecker) {
    super(lightOpenGlView);
    srsFlvMuxer = new SrsFlvMuxer(connectChecker);
  }

  public RtmpCamera2(Context context, boolean useOpengl, ConnectCheckerRtmp connectChecker) {
    super(context, useOpengl);
    srsFlvMuxer = new SrsFlvMuxer(connectChecker);
  }

  /**
   * H264 profile.
   *
   * @param profileIop Could be ProfileIop.BASELINE or ProfileIop.CONSTRAINED
   */
  public void setProfileIop(byte profileIop) {
    srsFlvMuxer.setProfileIop(profileIop);
  }

  public void resizeFlvTagCache(int newSize) {
    srsFlvMuxer.resizeFlvTagCache(newSize);
  }

  public int getFlvTagCacheSize(){ return srsFlvMuxer != null ? srsFlvMuxer.getFlvTagCacheSize() : -1; }

  public long getSentAudioFrames() { return srsFlvMuxer != null ? srsFlvMuxer.getSentAudioFrames() : -1; }
  public long getSentVideoFrames() { return srsFlvMuxer != null ? srsFlvMuxer.getSentVideoFrames() : -1; }
  public long getDroppedAudioFrames() { return srsFlvMuxer != null ? srsFlvMuxer.getDroppedAudioFrames() : -1; }
  public long getDroppedVideoFrames() { return srsFlvMuxer != null ? srsFlvMuxer.getDroppedVideoFrames() : -1; }

  public void resetSentAudioFrames() { if(srsFlvMuxer != null) { srsFlvMuxer.resetSentAudioFrames(); } }
  public void resetSentVideoFrames() { if(srsFlvMuxer != null) { srsFlvMuxer.resetSentVideoFrames(); } }
  public void resetDroppedAudioFrames() { if(srsFlvMuxer != null) { srsFlvMuxer.resetDroppedAudioFrames(); } }
  public void resetDroppedVideoFrames() { if(srsFlvMuxer != null) { srsFlvMuxer.resetDroppedVideoFrames(); } }

  @Override
  public void setAuthorization(String user, String password) {
    srsFlvMuxer.setAuthorization(user, password);
  }

  @Override
  protected void prepareAudioRtp(boolean isStereo, int sampleRate) {
    srsFlvMuxer.setIsStereo(isStereo);
    srsFlvMuxer.setSampleRate(sampleRate);
  }

  @Override
  protected void startStreamRtp(String url) {
    if (videoEncoder.getRotation() == 90 || videoEncoder.getRotation() == 270) {
      srsFlvMuxer.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
    } else {
      srsFlvMuxer.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
    }
    srsFlvMuxer.start(url);
  }

  @Override
  protected void stopStreamRtp() {
    srsFlvMuxer.stop();
  }

  @Override
  protected void getAacDataRtp(ByteBuffer aacBuffer, MediaCodec.BufferInfo info) {
    srsFlvMuxer.sendAudio(aacBuffer, info);
  }

  @Override
  protected void onSpsPpsVpsRtp(ByteBuffer sps, ByteBuffer pps, ByteBuffer vps) {
    srsFlvMuxer.setSpsPPs(sps, pps);
  }

  @Override
  protected void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
    srsFlvMuxer.sendVideo(h264Buffer, info);
  }
}

