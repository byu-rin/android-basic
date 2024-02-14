package com.byurin.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.byurin.notification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val CHANNEL_ID = "channel_id" // 알림 채널 아이디
    private val CHANNEL_NAME = "channel_name" // 채널 이름
    private val NOTIFICATION_ID = 0 // 알림 아이디
    private val REQUEST_NOTIFICATION_PERMISSION = 1 // 요청 코드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 알림 채널 생성
        notificationChannel()

        // 알림 클릭 시 실행한 Intent 생성
        val intent = Intent(this, MainActivity::class.java) // 현재 액티비티에서 다시 현재 액티비티로 이동하는 인텐트

        // API 레벨 따라 적절한 PendingIntent 생성
        val pendingIntent: PendingIntent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 레벨 23 이상인 경우, 불변 PendingIntent 생성
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            // API 레벨 22 이하인 경우, 기존 방식으로 PendingIntent 생성
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }


        // 알림 생성
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("뷰린이가 다가오고 있어요.") // 알림 제목
            .setContentText("뷰린이와 안드로이드 앱을 만들어보세요.") // 알림 내용
            .setSmallIcon(R.drawable.logo_icon) // 알림 아이콘
            .setContentIntent(pendingIntent) // 알림 클릭 시 실행할 PendingIntent
            .build()

        val notificationManagerCompat = NotificationManagerCompat.from(this)

        // show notification 클릭 시
        binding.notifyButton.setOnClickListener {
            // 알림 권한이 부여되어 있는지 확인
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS // 알림 보낼 권한 나타내는 상수
                ) != PackageManager.PERMISSION_GRANTED // 알림 보내는 권한 부여되지 않은 상태
            ) {
                // 알림 권한이 부여되어 있지 않다면, 알림 권한 설정 페이지로 이동
                val intent = Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS // 아림 권한 설정 페이지로 이동하는 액션
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName) // 현재 앱 패키지 이름 설정
                }
                startActivityForResult(intent, REQUEST_NOTIFICATION_PERMISSION) // 설정 페이지로 이동(알림권한 부여 or 거부 시 이전 화면으로 돌아옴.
            } else { // 알림 권한 부여되어 있는 상태
                notificationManagerCompat.notify(NOTIFICATION_ID, notification)
            }

        }
    }

    // 알림 채널 생성
    // API 26 이상인 경우 실행되는 알림 채널 생성하는 함수
    private fun notificationChannel() {
        // 디바이스가 oreo 이상인지 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // NotificationChannel 생성하고 채널 아이디, 채널 이름, 중요도 설정
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT // 중요도 레벨 기본
                //
            ).apply {
                lightColor = Color.GREEN
                enableLights(true) // 알림이 도착했을 때 화면 상단 LED 가 켜짐
            }
            // NotificationManager에 채널 등록(알림 표시할 때 이 채널 사용)
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            }
        }
    }
