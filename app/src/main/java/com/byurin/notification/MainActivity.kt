package com.byurin.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
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
        // TaskStackBuilder 는 인텐트 작업 스택 관리, 스택에 새로운 인테튼 추가하는 기능 제공
        val pendingIntent = TaskStackBuilder.create(this).run {
            // 현재 액티비티를 부모 스택에 추가(알림 클릭 시 이전 화면에 표시되었던 액티비티로 돌아갈 수 있다.)
            addNextIntentWithParentStack(intent)
            // 인텐트를 실행하는 PendingIntent 객체 가져옴.
            // PendingIntent.FLAG_UPDATE_CURRENT 는 이미 존재하는 PendingIntent가 있을 경우 해당 PendingIntent를 업데이트하여 사용하도록 설정
            // 인텐트가 실행될 때마다 동일한 PendingIntent가 사용, 새로운 인텐트가 이전 인텐트 대체
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // 알림 생성
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("뷰린이가 다가오고 있어요.") // 알림 제목
            .setContentText("뷰린이와 안드로이드 앱을 만들어보세요.") // 알림 내용
            .setSmallIcon(R.drawable.ic_launcher_foreground) // 알림 아이콘
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
