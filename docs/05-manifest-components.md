# Strava Android — Manifest & Component Inventory

**Source:** decoded `AndroidManifest.xml` (194,810 chars, JADX 0.7.1 via REA) · **Package:** `com.strava` 456.10

| | |
|---|---|
| Activities | **336** (+16 `activity-alias`) |
| Services | **23** |
| Broadcast receivers | **28** |
| Content providers | **16** |
| Intent filters | 215 (238 actions, 323 categories, 405 `<data>` rules) |
| Declared permissions | 43 `<uses-permission>` (+3 custom `<permission>`, 2 `uses-permission-sdk-23`) |
| `<meta-data>` | 56 |
| Uses-feature | 4 |

---

## 1. Application element

| Attribute | Value |
|---|---|
| `android:name` | `com.strava.StravaApplication` |
| `android:theme` | `@style/StravaTheme` |
| `android:label` | `@string/app_name` |
| `android:icon` | `@mipmap/ic_launcher_default` |
| `android:allowBackup` | `true` |
| `android:largeHeap` | `true` |
| `android:supportsRtl` | `false` |
| `android:resizeableActivity` | `false` |
| `android:extractNativeLibs` | `false` (uncompressed .so, loaded straight from the APK) |
| `android:fullBackupContent` | `@xml/backupscheme` |
| `android:networkSecurityConfig` | `@xml/network_security_config` |
| `android:localeConfig` | `@xml/locales_config` |
| `android:appComponentFactory` | `androidx.core.app.CoreComponentFactory` |

**`<uses-feature>`** — `android.hardware.camera` (not required), `android.hardware.wifi` (not required), `android.hardware.usb.host` (not required), plus one implicit (`required=true`).

---

## 2. Declared permissions (43)


| `android.permission.POST_NOTIFICATIONS` | — |
| `com.google.android.providers.gsf.permission.READ_GSERVICES` | — |
| `android.permission.ACCESS_FINE_LOCATION` | — |
| `android.permission.ACCESS_COARSE_LOCATION` | — |
| `android.permission.ACCESS_NETWORK_STATE` | — |
| `android.permission.FOREGROUND_SERVICE` | — |
| `android.permission.READ_MEDIA_VISUAL_USER_SELECTED` | — |
| `android.permission.READ_MEDIA_IMAGES` | — |
| `android.permission.READ_MEDIA_VIDEO` | — |
| `android.permission.READ_EXTERNAL_STORAGE` | 32 |
| `android.permission.WRITE_EXTERNAL_STORAGE` | 29 |
| `android.permission.RECEIVE_BOOT_COMPLETED` | — |
| `android.permission.VIBRATE` | — |
| `com.android.vending.BILLING` | — |
| `android.permission.USE_CREDENTIALS` | — |
| `android.permission.WAKE_LOCK` | — |
| `android.permission.READ_CONTACTS` | — |
| `com.strava.permission.C2D_MESSAGE` | — |
| `android.permission.ACCESS_MEDIA_LOCATION` | — |
| `com.google.android.gms.permission.AD_ID` | — |
| `android.permission.health.WRITE_EXERCISE` | — |
| `android.permission.health.WRITE_DISTANCE` | — |
| `android.permission.health.WRITE_TOTAL_CALORIES_BURNED` | — |
| `android.permission.health.READ_WEIGHT` | — |
| `android.permission.DETECT_SCREEN_CAPTURE` | — |
| `android.permission.GET_ACCOUNTS` | — |
| `android.permission.ACTIVITY_RECOGNITION` | — |
| `android.permission.BLUETOOTH` | 30 |
| `android.permission.BLUETOOTH_ADMIN` | 30 |
| `android.permission.SCHEDULE_EXACT_ALARM` | — |
| `android.permission.BLUETOOTH_SCAN` | — |
| `android.permission.BLUETOOTH_CONNECT` | — |
| `android.permission.FOREGROUND_SERVICE_LOCATION` | — |
| `android.permission.INTERNET` | — |
| `android.permission.RECORD_AUDIO` | — |
| `android.permission.CAMERA` | — |
| `com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE` | — |
| `android.permission.ACCESS_ADSERVICES_ATTRIBUTION` | — |
| `android.permission.ACCESS_ADSERVICES_AD_ID` | — |
| `com.google.android.c2dm.permission.RECEIVE` | — |
| `android.permission.READ_MEDIA_AUDIO` | — |
| `android.permission.ACCESS_WIFI_STATE` | — |
| `com.strava.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | — |

### Custom permissions declared by Strava

- `com.strava.permission.C2D_MESSAGE` — protectionLevel `signature`
- `com.strava.permission.SHARED_IDENTITY` — protectionLevel `signature`
- `com.strava.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` — protectionLevel `signature`

### `<queries>` — packages and intents the app may probe

- package `com.google.android.apps.maps`
- package `com.garmin.android.apps.connectmobile`
- intent action=['android.intent.action.SENDTO'] data=['mimeType=text/plain']
- intent action=['android.intent.action.SEND'] data=['mimeType=text/plain']
- intent action=['android.intent.action.SEND'] data=['mimeType=image/*']
- intent action=['android.intent.action.SEND_MULTIPLE'] data=['mimeType=image/*']
- intent action=['android.intent.action.TTS_SERVICE'] data=[]
- intent action=['android.intent.action.VIEW'] data=['scheme=http']
- package `com.google.android.apps.healthdata`
- intent action=['android.intent.action.MAIN'] data=[]
- intent action=['android.media.action.IMAGE_CAPTURE'] data=[]
- intent action=['android.intent.action.OPEN_DOCUMENT'] data=['mimeType=*/*']
- intent action=['android.intent.action.GET_CONTENT'] data=['mimeType=*/*']
- package `com.facebook.katana`
- intent action=['com.android.vending.billing.InAppBillingService.BIND'] data=[]
- intent action=['android.media.action.VIDEO_CAPTURE'] data=[]
- intent action=['android.intent.action.VIEW'] data=['scheme=http']
- intent action=['android.intent.action.GET_CONTENT'] data=[]

---

## 3. Activities (336)

`exported` values are as declared; a blank value means the attribute was absent (Android then infers it from intent filters).

| Activity | exported | intent filters |
|---|---|---|
| `com.strava.SplashActivity` | true | A:MAIN / C:LAUNCHER<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=,path=/<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=open<br>launchMode=singleTask |
| `com.strava.view.auth.WelcomeInvitedActivity` | false | — |
| `com.strava.view.onboarding.HealthConsentActivity` | false | — |
| `com.strava.view.onboarding.ConsentFlowDeviceConnectIntroActivity` | false | — |
| `com.strava.view.onboarding.DeviceConnectActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=settings,path=/connected-devices-updated<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=settings,pathPattern=/connected-devices-updated/.* |
| `com.strava.settings.view.email.EmailChangedVerificationActivity` | false | — |
| `com.strava.deviceconnect.connect.ThirdPartyConnectActivity` | false | — |
| `com.strava.view.clubs.ClubsIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=clubs,host=group_events<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPattern=/clubs/..*<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/clubs |
| `com.strava.posts.view.PostsIntentCatcherActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=posts,pathPattern=/.*<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPattern=/posts/.* |
| `com.strava.posts.view.composer.AthleteAddPostActivity` | false | — |
| `com.strava.view.segments.SegmentsIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/segments<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=segments |
| `com.strava.view.bottomnavigation.BottomNavigationActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/dashboard<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=dashboard,path=,scheme=strava,host=dashboardYou<br>launchMode=singleTop |
| `com.strava.settings.view.SettingsActivity` | true | A:MAIN / C:NOTIFICATION_PREFERENCES<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path= |
| `com.strava.settings.view.PrivacySettingProfileActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/settings/privacy/profile-page<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/profile-page |
| `com.strava.settings.view.PrivacySettingActivitiesActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/privacy/activities<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/activities |
| `com.strava.settings.view.PrivacySettingGroupedActivitiesActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/privacy/grouped-activities<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/grouped-activities |
| `com.strava.settings.view.PrivacySettingFlybyActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/privacy/flyby<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/flyby |
| `com.strava.settings.view.PrivacySettingLocalLegendsActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/privacy/local-legend<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/local-legend |
| `com.strava.settings.view.MetroHeatmapPreferenceActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/privacy/metro-heatmap<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/metro-heatmap |
| `com.strava.settings.view.FeedOrderingSettingsActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/feed-ordering<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/feed-ordering |
| `com.strava.settings.view.HealthDataSettingsActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/data-permissions<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/data-permissions |
| `com.strava.settings.view.EmailPromotionSettingsActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/email-notifications<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/email-notifications |
| `com.strava.view.connect.ThirdPartySettingsActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/connected-apps<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/connected-apps |
| `com.strava.settings.view.password.PasswordChangeActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/change-password<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/change-password |
| `com.strava.settings.view.PartnerIntegrationOptOutActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPattern=/settings/sponsored-partners/opt-out/.*<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,pathPattern=/sponsored-partners/opt-out/.* |
| `com.strava.settings.view.PartnerIntegrationsActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/sponsored-partners<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/sponsored-partners |
| `com.strava.settings.view.DisplayPreferenceActivity` | false | — |
| `com.strava.settings.view.PushNotificationSettingsActivity` | false | — |
| `com.strava.settings.view.ContactsSyncPreferenceActivity` | false | — |
| `com.strava.settings.view.LegalPreferenceActivity` | false | — |
| `com.strava.settings.view.AboutSettingsActivity` | false | — |
| `com.strava.settings.view.defaultmaps.DefaultMapsPreferenceActivity` | false | — |
| `com.facebook.CustomTabActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=fb284597785309<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=fbconnect,host=cct.com.strava |
| `com.facebook.FacebookActivity` | false | — |
| `com.strava.view.ImageViewActivity` | false | — |
| `com.strava.profile.view.ProfileModularActivity` | false | launchMode=singleTop |
| `com.strava.view.athletes.AthletesIntentCatcherActivity` | true | A:VIEW / C:DEFAULT / D:scheme=strava,host=athletes,pathPattern=/.*<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/athletes/<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/athlete/<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=athlete<br>A:VIEW / C:DEFAULT / D:scheme=strava,host=pros,pathPattern=/.*<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/pros/ |
| `com.strava.view.onboarding.DeviceConnectIntentCatcherActivity` | true | A:VIEW,VIEW / C:DEFAULT,BROWSABLE,DEFAULT,BROWSABLE / D:scheme=strava,host=settings,path=/connected-devices<br>A:VIEW,VIEW / C:DEFAULT,BROWSABLE,DEFAULT,BROWSABLE / D:scheme=strava,host=settings,pathPattern=/connected-devices/.* |
| `com.strava.view.activities.ShareIntentCatcherActivity` | false | — |
| `com.strava.view.activities.TextShareCatcherActivity` | true | A:SEND / C:DEFAULT / D:mimeType=text/plain |
| `com.strava.view.activities.ImageShareCatcherActivity` | true | A:SEND / C:DEFAULT / D:mimeType=image/*<br>A:SEND_MULTIPLE / C:DEFAULT / D:mimeType=image/* |
| `com.strava.view.activities.ActivitiesIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=activities<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/activities,pathPrefix=/rides,pathPrefix=/runs,pathPrefix=/walks,pathPrefix=/hikes |
| `com.strava.view.superuser.SuperUserToolsActivity` | false | — |
| `com.strava.view.connect.GarminConnectActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=https,host=www.strava.com,pathPattern=/o_auth/garmin/mobile_connected<br>launchMode=singleTop |
| `com.strava.view.connect.FitbitConnectActivity` | false | — |
| `com.strava.util.RecordingIntentCatcherActivity` | true | A:NDEF_DISCOVERED / C:DEFAULT / D:scheme=http,host=strava.com,pathPrefix=/nfc<br>A:RUN / C:DEFAULT / D:scheme=http,host=strava.com,pathPrefix=/nfc<br>A:FINISH / C:DEFAULT / D:scheme=http,host=strava.com,pathPrefix=/nfc<br>A:TRACK / C:DEFAULT / D:mimeType=vnd.google.fitness.activity/biking<br>A:TRACK / C:DEFAULT / D:mimeType=vnd.google.fitness.activity/running<br>A:TRACK / C:DEFAULT / D:mimeType=vnd.google.fitness.activity/other |
| `com.strava.settings.view.HuevoDePascua` | false | — |
| `com.strava.forceupdate.ForceUpdatePopupActivity` | false | — |
| `com.strava.view.activities.BranchIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=strava.app.link |
| `com.strava.notifications.NotificationExternalIntentDispatcher` | true | A:VIEW / C:DEFAULT / D:scheme=strava,host=external |
| `com.strava.healthconnect.HealthConnectPermissionsActivity` | true | A:ACTION_SHOW_PERMISSIONS_RATIONALE |
| `com.strava.healthconnect.HealthConnectEducationActivity` | false | — |
| `com.strava.workout.detail.generic.GenericWorkoutViewActivity` | false | — |
| `com.strava.activitydetail.view.ActivityDescriptionActivity` | false | — |
| `com.strava.activitydetail.crop.ActivityCropActivity` | false | — |
| `com.strava.activitydetail.medialist.ActivityMediaListActivity` | false | — |
| `com.strava.activitydetail.view.kudos.KudoListActivity` | false | — |
| `com.strava.activitydetail.streamcorrection.StreamCorrectionActivity` | false | — |
| `com.strava.activitydetail.view.MatchedActivitiesActivity` | false | — |
| `com.strava.activitydetail.power.ui.ActivityPowerCurveActivity` | false | — |
| `com.strava.activitydetail.universal.AdpActivity` | false | — |
| `com.strava.activitydetail.results.ActivityResultsActivity` | false | — |
| `com.strava.activitysave.ui.SaveActivity` | false | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=save-screen |
| `com.strava.activitysave.quickedit.view.QuickEditActivity` | false | — |
| `com.strava.activitysave.quickeditsettings.QuickEditSettingsActivity` | false | — |
| `com.strava.activitysave.postupload.PostUploadActivity` | false | — |
| `com.strava.activitycalendar.ActivityCalendarActivity` | false | — |
| `com.strava.ageassurance.BirthdayInputActivity` | false | A:VIEW / C:DEFAULT / D:scheme=strava,host=age-assurance,path=/birthday-input |
| `com.strava.challenges.su.ChallengeCompletionAdminActivity` | false | — |
| `com.strava.challenges.activitylist.ChallengeActivityListActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=challenges,path=/activity-list |
| `com.strava.challenges.ChallengeCelebrationActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=action,host=challenges,path=/challenge-celebration |
| `com.strava.challenges.participants.ChallengeParticipantsListActivity` | false | — |
| `com.strava.challenges.ChallengeTermsActivity` | false | — |
| `com.strava.challenges.intent.ChallengeIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPattern=/challenges/.*<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=challenges,pathPattern=/.*<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/challenges<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=challenges,path= |
| `com.strava.challenges.ChallengeIndividualModularActivity` | false | — |
| `com.strava.challenges.challengedetail.ChallengeDetailActivity` | false | — |
| `com.strava.chats.ChatIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/chats<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=chats |
| `com.strava.chats.ChatActivity` | false | — |
| `com.strava.chats.chatlist.ChatListActivity` | true | A:SEND / C:DEFAULT / D:mimeType=text/plain |
| `com.strava.chats.settings.ChatSettingsActivity` | false | — |
| `com.strava.chats.rename.RenameChannelActivity` | false | — |
| `com.strava.chats.clubchannels.presentation.CreateClubChannelActivity` | false | — |
| `com.strava.chats.clubchannellist.view.ClubChannelListActivity` | false | — |
| `com.strava.clubs.groupevents.GroupEventAttendeeListActivity` | false | — |
| `com.strava.clubs.groupevents.GroupEventEditActivity` | false | — |
| `com.strava.clubs.members.ClubMembersActivity` | false | — |
| `com.strava.clubs.detail.ClubDetailModularActivity` | false | — |
| `com.strava.clubs.posts.view.PostFeedModularActivity` | false | — |
| `com.strava.clubs.groupevents.detail.LegacyGroupEventDetailActivity` | false | — |
| `com.strava.clubs.groupevents.detailv2.view.EventDetailActivity` | false | — |
| `com.strava.clubs.feed.ClubFeedActivity` | false | — |
| `com.strava.clubs.search.ClubsSearchActivity` | false | — |
| `com.strava.clubs.leaderboard.ClubLeaderboardActivity` | false | — |
| `com.strava.clubs.information.ClubInformationActivity` | false | — |
| `com.strava.clubs.settings.ClubSettingsActivity` | false | — |
| `com.strava.clubs.create.CreateClubActivity` | false | — |
| `com.strava.clubs.create.view.EditClubActivity` | false | — |
| `com.strava.clubs.groupevents.view.EventHistoryActivity` | false | — |
| `com.strava.clubs.groupevents.EventInsightsActivity` | false | — |
| `com.strava.clubs.groupevents.ui.EventAttendeesActivity` | false | — |
| `com.strava.clubs.groupevents.create.view.EventCreateEditActivity` | false | — |
| `com.strava.clubs.groupevents.list.EventListActivity` | false | — |
| `com.strava.clubs.waivers.view.WaiverListActivity` | false | — |
| `com.strava.clubs.waivers.view.CreateWaiverActivity` | false | — |
| `com.strava.clubs.waivers.view.WaiverSigningActivity` | false | — |
| `com.strava.clubs.waivers.view.WaiverDetailActivity` | false | — |
| `com.strava.clubs.waivers.view.WaiverSignaturesListActivity` | false | — |
| `com.strava.clubs.groupevents.search.view.EventSearchActivity` | false | — |
| `com.strava.clubs.groupevents.search.view.EventFiltersActivity` | false | — |
| `com.strava.posts.view.PostKudosListActivity` | false | — |
| `com.strava.posts.view.SingleAthletePostsActivity` | false | — |
| `com.strava.posts.view.postdetailv2.PostDetailActivityV2` | false | — |
| `com.strava.posts.view.composer.ClubAddPostActivity` | false | — |
| `com.strava.posts.medialist.PostMediaListActivity` | false | — |
| `com.strava.comments.CommentsActivity` | false | — |
| `com.strava.comments.reactions.CommentReactionsModalActivity` | false | — |
| `com.strava.comments.CommentsIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/comments<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=comments |
| `com.strava.comments.activitycomments.ActivityCommentsActivity` | false | — |
| `com.strava.comments.ugcalert.UgcAlertActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=ugc-alert |
| `com.strava.onboarding.ftuef.ui.OnboardingActivity` | false | — |
| `com.strava.onboarding.view.CompleteProfileIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=second_mile,pathPrefix=/complete_profile |
| `com.strava.onboarding.OnboardingIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=onboarding |
| `com.strava.communitysearch.view.invite.ClubsAndAthleteSearchActivity` | false | — |
| `com.strava.communitysearch.view.athletesearch.AthleteSearchActivity` | false | — |
| `com.strava.communitysearch.view.search.RecentSearchesActivity` | false | — |
| `com.strava.feed.view.AthleteRelationshipModalActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=super-follows |
| `com.strava.feed.view.modal.GroupedActivitiesModalActivity` | false | — |
| `com.strava.notificationsui.NotificationListActivity` | false | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=notifications |
| `com.strava.profile.view.AthleteConnectionsActivity` | false | — |
| `com.strava.profile.view.AthleteStatsActivity` | false | — |
| `com.strava.profile.medialist.AthleteMediaListActivity` | false | — |
| `com.strava.profile.report.ReportProfileActivity` | false | — |
| `com.strava.profile.view.SingleAthleteFeedActivity` | false | — |
| `com.strava.profile.ProfileEditActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/profile<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=settings,path=/profile |
| `com.strava.settings.view.PrivacyCenterActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/privacy<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy |
| `com.strava.settings.view.NotificationSettingsActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=notification-settings |
| `com.strava.settings.view.privacyzones.PrivacyZonesActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/privacy/zones<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/zones<br>launchMode=singleTask |
| `com.strava.settings.view.privacyzones.AddPrivacyZoneActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/zones/add |
| `com.strava.settings.view.privacyzones.HideStartEndSelectionActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/maps |
| `com.strava.settings.view.privacyzones.HideStartEndDistanceActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/maps/distance |
| `com.strava.settings.view.privacyzones.HideEntireMapActivity` | false | — |
| `com.strava.settings.view.PrivacySettingMentionsActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/privacy/mentions<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/mentions |
| `com.strava.settings.view.messaging.MessagingSettingsActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/privacy/messaging<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/privacy/messaging |
| `com.strava.settings.view.statprivacy.StatPrivacyActivity` | true | — |
| `com.strava.settings.support.TicketDeflectionActivity` | false | — |
| `com.strava.settings.view.privacyzones.LocalHideStartEndActivity` | false | — |
| `com.strava.settings.view.pastactivityeditor.PastActivitiesEditorActivity` | false | — |
| `com.strava.settings.view.blocking.BlockedAthletesActivity` | false | — |
| `com.strava.settings.view.weather.AboutWeatherActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/weather-visibility |
| `com.strava.settings.view.email.EmailConfirmationActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/email_verification<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=email_verification,pathPrefix= |
| `com.strava.settings.view.email.v2.EmailChangeV2Activity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/change-email<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=settings,path=/change-email |
| `com.strava.settings.view.phone.PhoneNumberActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=settings,path=/phone_number |
| `com.strava.settings.view.otp.SwitchToOtpActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=settings,path=/opt_in_to_otp |
| `com.strava.settings.view.personalinformation.PersonalInformationShareActivity` | false | — |
| `com.strava.settings.view.challenges.ChallengeParticipationActivity` | false | — |
| `com.strava.settings.view.appearance.AppearanceActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/settings/appearance<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=settings,path=/appearance |
| `com.strava.athleteworkouts.AthleteWorkoutsActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=workouts |
| `com.strava.authorization.view.welcomeCarouselActivity.WelcomeCarouselActivity` | false | — |
| `com.strava.authorization.oauth.OauthIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=www.strava.com,host=strava.com,path=/oauth/mobile/authorize<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=www.strava.com,host=strava.com,pathPattern=/login/link<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=login<br>A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=signup |
| `com.strava.authorization.oauth.OAuthActivity` | false | launchMode=singleTop |
| `com.strava.authorization.loginorsignup.LogInOrSignUpActivity` | true | launchMode=singleTop |
| `com.strava.authorization.otp.OtpAndPasswordAuthActivity` | true | launchMode=singleTop |
| `com.strava.authorization.facebook.deprecation.FacebookDeprecationModalActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=facebook,path=/deprecation |
| `com.strava.authorization.facebook.deprecation.login.FacebookDeprecationLoginActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=facebook,path=/deprecation/login |
| `com.strava.authorization.facebook.deprecation.confirmemail.FacebookDeprecationConfirmEmailActivity` | false | — |
| `com.strava.authorization.facebook.deprecation.changeemail.FacebookDeprecationChangeEmailActivity` | false | — |
| `com.strava.bestefforts.ui.history.BestEffortsHistoryActivity` | false | — |
| `com.strava.bestefforts.ui.details.BestEffortsDetailsActivity` | false | — |
| `com.strava.fitness.FitnessActivity` | false | — |
| `com.strava.fitness.modal.FitnessActivityListActivity` | false | — |
| `com.strava.fitness.progress.analysis.ProgressAnalysisActivity` | false | — |
| `com.strava.flyover.FlyoverActivity` | false | — |
| `com.strava.flyover.injection.FlyoverIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=flyover |
| `com.strava.widget.glance.configuration.GoalWidgetConfigurationActivity` | true | A:VIEW / C:DEFAULT / D:scheme=strava,host=widget,path=/config |
| `com.strava.widget.glance.configuration.DetailedGoalWidgetConfigurationActivity` | true | A:VIEW / C:DEFAULT / D:scheme=strava,host=widget,path=/config-detailed |
| `com.strava.widget.superuser.GoalsWidgetSuperUserActivity` | false | — |
| `com.strava.goals.add.AddGoalActivity` | false | — |
| `com.strava.goals.list.GoalListActivity` | false | — |
| `com.strava.goals.edit.EditGoalActivity` | false | — |
| `com.strava.goals.edit.GoalsBottomSheetActivity` | false | — |
| `com.strava.groups.GroupsIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=groups |
| `com.strava.mapstab.ftux.PoiFtuxActivity` | false | — |
| `com.strava.mapstab.ftux.RouteBuilderFtuxActivity` | false | — |
| `com.strava.mapstab.ftux.SurfaceTypeFtuxActivity` | false | — |
| `com.strava.routebuilder.RouteBuilderActivity` | true | — |
| `com.strava.routeslistsaved.SavedRoutesActivity` | false | — |
| `com.strava.superuser.metering.ManageMeteringActivity` | false | — |
| `com.strava.superuser.AnalyticsCacheActivity` | false | — |
| `com.strava.superuser.DebugToolsSettingsActivity` | false | — |
| `com.strava.superuser.ChangeLanguageActivity` | false | — |
| `com.strava.superuser.subscription.ToggleSubscriptionFragmentActivity` | false | — |
| `com.strava.routing.utils.MapCacheClearingActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=clear_map_cache |
| `com.strava.routing.deeplinks.intentHandling.RoutesIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=routing,pathPattern=/suggested_routes/.*<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=routing,pathPrefix=/suggested<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=routing,path=/ephemeral<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=routing,path=/edit<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=routing,path=/saved<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/routes<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=routes,pathPattern=/.*<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=maps,pathPattern=/.* |
| `com.strava.routing.save.legacy.RouteSaveActivity` | true | — |
| `com.strava.routing.legacy.oldRoutesList.RouteListActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=routes,path= |
| `com.strava.routing.legacy.oldRoutesList.LegacyRouteDetailActivity` | true | — |
| `com.strava.routing.mediaList.RouteMediaListActivity` | false | — |
| `com.strava.segments.SegmentActivity` | false | — |
| `com.strava.segments.SegmentMapActivity` | false | — |
| `com.strava.segments.trendline.SegmentEffortTrendLineActivity` | false | — |
| `com.strava.segments.efforts.SegmentEffortsActivity` | false | — |
| `com.strava.segments.leaderboards.LeaderboardsActivity` | false | — |
| `com.strava.segments.locallegends.LocalLegendsActivity` | false | — |
| `com.strava.segments.segmentslists.SegmentsListsActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=segmentslists,path=/ |
| `com.strava.traininglog.ui.TrainingLogActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=training,pathPrefix=/log |
| `com.strava.subscriptionsui.screens.studentplan.StudentPlanDialogFragmentActivity` | true | A:VIEW / C:DEFAULT / D:scheme=strava,host=student_plan,path=/dialog |
| `com.strava.subscriptionsui.screens.studentplan.StudentPlanActivity` | false | — |
| `com.strava.subscriptionsui.SubscriptionIntentDispatcher` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=premium<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=subscription<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/premium,pathPrefix=/subscription/checkout |
| `com.strava.subscriptionsui.screens.management.SubscriptionManagementActivity` | false | — |
| `com.strava.subscriptionsui.screens.superuser.rive.SubscriptionsRiveSuperUserActivity` | false | — |
| `com.strava.subscriptionsui.screens.checkout.CheckoutActivity` | false | — |
| `com.strava.subscriptionsui.screens.checkout.cart.CheckoutCartActivity` | false | — |
| `com.strava.subscriptionsui.screens.preview.welcome.SubPreviewWelcomeSheetFragmentActivity` | false | — |
| `com.strava.subscriptionsui.screens.preview.hub.SubPreviewHubActivity` | false | — |
| `com.strava.subscriptionsui.screens.overview.SubscriptionOverviewActivity` | false | — |
| `com.strava.subscriptionsui.screens.trialeducation.hub.TrialEducationHubActivity` | false | — |
| `com.strava.subscriptionsui.screens.trialeducation.pager.TrialEducationPagerActivity` | false | — |
| `com.strava.subscriptionsui.screens.cancellation.SubscriptionCancellationActivity` | false | — |
| `com.strava.subscriptionsui.screens.customappicons.CustomAppIconsActivity` | false | — |
| `com.strava.subscriptionsui.screens.crossgrading.CrossgradingPromotionActivity` | false | — |
| `com.strava.subscriptionsui.screens.deviceconnect.DeviceConnectUpsellActivity` | false | — |
| `com.strava.subscriptionsui.screens.checkout.unified.UnifiedCheckoutActivity` | false | — |
| `com.strava.subscriptionsui.screens.postcheckout.UnifiedPostCheckoutActivity` | false | — |
| `com.strava.subscriptionsui.screens.superuser.SubscriptionSuperUserToolsActivity` | false | — |
| `com.strava.subscriptionsui.screens.superuser.subscriptiondetail.SubscriptionDetailSuperUserActivity` | false | — |
| `com.strava.subscriptionsui.screens.interstitial.runna.RunnaInterstitialPromotionActivity` | true | A:VIEW / C:DEFAULT / D:scheme=strava,host=runna,path=/interstitial |
| `com.strava.insights.view.InsightsActivity` | false | launchMode=singleTop |
| `com.strava.insights.view.InsightsIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=training,path=/relative-effort |
| `com.strava.performancepredictions.PerformancePredictionsActivity` | false | — |
| `com.strava.powerprofile.PowerProfileActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=power-profile,pathPrefix=/log |
| `com.strava.recordingui.legacy.RecordShortcutIntentCatcher` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=record_shortcut,path= |
| `com.strava.recordingui.RecordIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=record,path= |
| `com.strava.recordingui.RecordingScreenActivity` | false | — |
| `com.strava.recordingui.settings.SensorSettingsActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=record,path=/settings/sensors |
| `com.strava.recordingui.settings.RecordSettingsActivity` | false | — |
| `com.strava.recordingui.settings.AutoPauseSettingsActivity` | false | — |
| `com.strava.recordingui.settings.AudioCuesSettingsActivity` | false | — |
| `com.strava.recordingui.settings.ScreenDisplaySettingsActivity` | false | — |
| `com.strava.recordingui.settings.sensors.LiveSegmentSettingsActivity` | false | — |
| `com.strava.recordingui.settings.RouteAlertsSettingsActivity` | false | — |
| `com.strava.recordingui.legacy.beacon.BeaconContactSelectionActivity` | false | — |
| `com.strava.recordingui.legacy.beacon.LiveTrackingPreferencesActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,path=/beacon/settings<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=beacon,path=/settings |
| `com.strava.recordingui.legacy.UnsyncedActivitiesActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=unsynced-activities |
| `com.strava.trainingzones.intent.TrainingZonesIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=settings,path=/training_zones |
| `com.strava.trainingzones.TrainingZonesSummaryActivity` | false | — |
| `com.strava.zendesk.ZendeskArticleLaunchingActivity` | true | A:VIEW_ARTICLE / C:DEFAULT<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=support |
| `zendesk.support.guide.HelpCenterActivity` | false | — |
| `zendesk.support.guide.ViewArticleActivity` | false | — |
| `zendesk.support.request.RequestActivity` | false | — |
| `zendesk.support.requestlist.RequestListActivity` | false | — |
| `com.strava.map.tools.MapSettingsActivity` | false | — |
| `com.strava.mapplayground.MapPlaygroundActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=map-playground,pathPattern=/.* |
| `com.strava.placesearch.search.PlaceSearchActivity` | false | — |
| `com.strava.placesearch.coordinatepicker.CoordinatePickerActivity` | false | — |
| `com.strava.routedetail.RouteDetailIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=standalone-route |
| `com.strava.routedetail.RouteDetailActivity` | false | — |
| `com.strava.reporting.ReportingActivity` | false | — |
| `com.strava.reporting.confirmation.ReportingConfirmationActivity` | false | — |
| `com.strava.sharing.view.ShareSheetActivity` | false | — |
| `com.strava.sharing.activity.ActivitySharingActivity` | false | — |
| `com.strava.sharing.view.PostableClubsListModularActivity` | false | — |
| `com.strava.sharing.polylinedemo.PolylineDemoActivity` | false | — |
| `com.strava.competitions.CompetitionsIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/competitions<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=competitions |
| `com.strava.competitions.detail.CompetitionDetailActivity` | false | — |
| `com.strava.competitions.create.CreateCompetitionActivity` | false | — |
| `com.strava.competitions.settings.CompetitionSettingsActivity` | false | — |
| `com.strava.competitions.settings.edit.EditCompetitionActivity` | false | — |
| `com.strava.competitions.settings.rules.CompetitionRulesActivity` | false | — |
| `com.strava.competitions.templates.CompetitionTemplateActivity` | false | — |
| `com.strava.competitions.medialist.CompetitionMediaListActivity` | false | — |
| `com.strava.athletemanagement.AthleteManagementActivity` | false | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=athlete-management |
| `com.strava.athleteselection.ui.AthleteSelectionActivity` | false | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=athlete-selection |
| `com.strava.featureswitchtools.experiments.OverrideExperimentCohortActivity` | false | — |
| `com.strava.featureswitchtools.ManageFeatureSwitchActivity` | false | — |
| `com.strava.yearinsport.ui.YearInSportComposeActivity` | true | — |
| `com.strava.yearinsport.share.YearInSportShareActivity` | false | — |
| `com.strava.yearinsport.share.CustomizeActivity` | false | — |
| `com.strava.photos.picker.MediaPickerActivity` | false | — |
| `com.strava.photos.categorypicker.GalleryCategoryPickerActivity` | false | — |
| `com.strava.photos.GalleryPreviewActivity` | false | — |
| `com.strava.photos.PhotoLightboxEditCaptionActivity` | false | — |
| `com.strava.photos.fullscreen.FullscreenMediaActivity` | false | — |
| `com.strava.photos.medialist.AthleteFullscreenMediaActivity` | false | — |
| `com.strava.photos.medialist.UnpaginatedFullscreenMediaActivity` | false | — |
| `com.strava.feedback.survey.FeedbackSurveyActivity` | false | — |
| `com.strava.feedback.FeedbackIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=feedback |
| `com.strava.gear.add.AddGearActivity` | false | — |
| `com.strava.gear.list.AthleteGearActivity` | false | — |
| `com.strava.gear.retire.RetiredGearListActivity` | false | — |
| `com.strava.gear.edit.shoes.EditShoesActivity` | false | — |
| `com.strava.gear.edit.bike.EditBikeActivity` | false | — |
| `com.strava.invites.ui.InviteActivity` | false | — |
| `com.strava.modularframeworkui.screen.ModularUiActivity` | false | — |
| `com.strava.modularframeworkui.sheet.ModularUiBottomSheetActivity` | false | — |
| `com.strava.modularframeworkui.sheet.ModularSheetIntentCatcherActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=http,scheme=https,host=app.strava.com,host=www.strava.com,host=strava.com,pathPrefix=/modular-sheet<br>A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=modular-sheet |
| `com.strava.monthlystats.MonthlyStatsActivity` | true | A:VIEW / C:BROWSABLE,DEFAULT / D:scheme=strava,host=monthly-stats |
| `com.strava.monthlystats.share.ShareActivity` | false | — |
| `com.strava.nettools.NetworkSettingsActivity` | false | — |
| `com.strava.nettools.canaries.ServiceCanaryListActivity` | false | — |
| `com.strava.nettools.canaries.ServiceCanaryConfigurationActivity` | false | — |
| `com.strava.nettools.NetworkLogActivity` | false | — |
| `com.strava.nettools.NetworkLogDetailActivity` | false | — |
| `com.strava.nettools.language.OverrideLanguageHeaderActivity` | false | — |
| `com.strava.search.ui.SearchActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=activity-search |
| `com.strava.celebrations.view.CelebrationActivity` | false | — |
| `com.strava.strengthtraining.musclegroupselection.MuscleGroupSelectionActivity` | false | — |
| `com.strava.sharinginterface.CopyLinkToClipboardActivity` | false | — |
| `com.strava.sharinginterface.CopyMediaToClipboardActivity` | false | — |
| `com.strava.sharinginterface.media.ExportFileActivity` | false | — |
| `com.strava.sharinginterface.qr.QRActivity` | false | — |
| `com.strava.modularframework.tools.DebugToolsActivity` | false | — |
| `com.strava.view.dialog.activitylist.ActivityListActivity` | false | — |
| `com.strava.googlefit.GoogleFitConnectActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=google-fit-connect |
| `com.strava.trainingplans.ui.TrainingPlansActivity` | true | A:VIEW / C:DEFAULT,BROWSABLE / D:scheme=strava,host=runna-training-plans |
| `com.strava.photoutils.cropping.PhotoCropActivity` | false | — |
| `com.strava.iterable.IterableNotificationsActivity` | false | — |
| `com.strava.facebook.FacebookPermissionsStubActivity` | false | — |
| `io.getstream.chat.android.ui.feature.gallery.AttachmentGalleryActivity` | false | — |
| `io.getstream.chat.android.ui.feature.gallery.AttachmentMediaActivity` | false | — |
| `io.getstream.chat.android.ui.feature.gallery.AttachmentActivity` | false | — |
| `io.getstream.chat.android.ui.feature.channels.ChannelListActivity` | false | — |
| `io.getstream.chat.android.ui.feature.messages.MessageListActivity` | false | — |
| `io.getstream.chat.android.compose.ui.attachments.preview.MediaPreviewActivity` | false | — |
| `io.getstream.chat.android.compose.ui.attachments.preview.MediaGalleryPreviewActivity` | false | — |
| `com.android.billingclient.api.ProxyBillingActivity` | false | — |
| `com.google.android.gms.auth.api.signin.internal.SignInHubActivity` | false | — |
| `com.facebook.CustomTabMainActivity` | false | — |
| `com.android.billingclient.api.ProxyBillingActivityV2` | false | — |
| `androidx.core.google.shortcuts.TrampolineActivity` | true | A:SHORTCUT_LISTENER |
| `com.google.android.gms.common.api.GoogleApiActivity` | false | — |
| `io.getstream.chat.android.ui.common.feature.documents.AttachmentDocumentActivity` | false | — |
| `androidx.glance.appwidget.action.ActionTrampolineActivity` | false | — |
| `androidx.glance.appwidget.action.InvisibleActionTrampolineActivity` | false | launchMode=singleInstance |
| `zendesk.classic.messaging.MessagingActivity` | false | — |
| `androidx.compose.ui.tooling.PreviewActivity` | true | — |
| `com.google.android.play.core.common.PlayCoreDialogWrapperActivity` | false | — |

### Activity aliases (16)

| Alias | target | purpose |
|---|---|---|
| `com.strava.SplashActivityLocalLegendIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityXomIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityTrophyIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityMedalIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityHikingIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityCyclingIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityRunningIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityKudosIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityStickerIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityPrideIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityRoadIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityGravelIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityTealIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityRedIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.SplashActivityBlackIcon` | com.strava.SplashActivity | LAUNCHER icon variant |
| `com.strava.healthconnect.ViewPermissionUsageActivity` | com.strava.healthconnect.HealthConnectPermissionsActivity | Health Connect permission-usage alias |

---

## 4. Services (23)

| Service | exported | foregroundServiceType | permission | actions |
|---|---|---|---|---|
| `com.strava.service.LiveTrackingSettingsUpdateService` | false | — | — | — |
| `com.google.assistant.appactions.widgets.pinappwidget.PinAppWidgetService` | true | — | — | com.google.assistant.appactions.widgets.PIN_APP_WIDGET, com.google.android.assistant.appactions.widgets.PIN_APP_WIDGET |
| `com.strava.WearMessageListener` | true | — | — | com.google.android.gms.wearable.MESSAGE_RECEIVED |
| `com.strava.onboarding.service.OnboardingService` | true | — | android.permission.BIND_JOB_SERVICE | — |
| `com.strava.recording.StravaActivityService` | — | location | — | — |
| `com.strava.recording.RestartRecordingJobService` | false | — | android.permission.BIND_JOB_SERVICE | — |
| `com.strava.notificationsgcm.StravaMessagingService` | false | — | — | com.google.firebase.MESSAGING_EVENT |
| `com.strava.iterable.IterableNotificationTrackingService` | true | — | android.permission.BIND_JOB_SERVICE | — |
| `com.google.android.gms.auth.api.signin.RevocationBoundService` | true | — | com.google.android.gms.auth.api.signin.permission.REVOCATION_NOTIFICATION | — |
| `com.google.firebase.components.ComponentDiscoveryService` | false | — | — | — |
| `com.google.android.gms.measurement.AppMeasurementService` | false | — | — | — |
| `com.google.android.gms.measurement.AppMeasurementJobService` | false | — | android.permission.BIND_JOB_SERVICE | — |
| `com.google.firebase.messaging.FirebaseMessagingService` | false | — | — | com.google.firebase.MESSAGING_EVENT |
| `androidx.work.impl.foreground.SystemForegroundService` | false | shortService | — | — |
| `androidx.room.MultiInstanceInvalidationService` | false | — | — | — |
| `androidx.glance.appwidget.GlanceRemoteViewsService` | true | — | android.permission.BIND_REMOTEVIEWS | — |
| `androidx.work.impl.background.systemalarm.SystemAlarmService` | false | — | — | — |
| `androidx.work.impl.background.systemjob.SystemJobService` | true | — | android.permission.BIND_JOB_SERVICE | — |
| `androidx.health.platform.client.impl.sdkservice.HealthDataSdkService` | true | — | — | androidx.health.platform.client.ACTION_BIND_SDK_SERVICE |
| `androidx.core.widget.RemoteViewsCompatService` | — | — | android.permission.BIND_REMOTEVIEWS | — |
| `com.mapbox.common.LifecycleService` | false | — | — | — |
| `com.google.android.datatransport.runtime.backends.TransportBackendDiscovery` | false | — | — | — |
| `com.google.android.datatransport.runtime.scheduling.jobscheduling.JobInfoSchedulerService` | false | — | android.permission.BIND_JOB_SERVICE | — |

---

## 5. Broadcast receivers (28)

| Receiver | exported | actions |
|---|---|---|
| `com.strava.recording.RecoverActivityReceiver` | false | android.intent.action.BOOT_COMPLETED, android.intent.action.MY_PACKAGE_REPLACED |
| `com.google.assistant.appactions.widgets.pinappwidget.PinAppWidgetBroadcastReceiver` | false | com.google.assistant.appactions.widgets.COMPLETE_PIN_APP_WIDGET, com.google.android.assistant.appactions.widgets.COMPLETE_PIN_APP_WIDGET |
| `com.strava.widget.providers.StravaAppWidgetProvider` | false | android.appwidget.action.APPWIDGET_UPDATE, com.strava.widget.refresh, com.strava.widget.startRecording, com.strava.recording.recordingStatsChangeAction, com.strava.recording.recordingStateChangeAction |
| `com.strava.widget.glance.receiver.DetailedGoalWidgetReceiver` | true | android.appwidget.action.APPWIDGET_UPDATE |
| `com.strava.widget.glance.receiver.GoalWidgetReceiver` | true | android.appwidget.action.APPWIDGET_UPDATE |
| `com.strava.widget.glance.receiver.StreaksSnapshotWidgetReceiver` | true | android.appwidget.action.APPWIDGET_UPDATE |
| `com.strava.notifications.NotificationSwipedReceiver` | false | com.strava.notifications.PushNotificationManager.NOTIFICATION_DISMISSED |
| `com.strava.androidextensions.web.CopyToClipBoardBroadcastReceiver` | false | — |
| `zendesk.support.DeepLinkingBroadcastReceiver` | false | zendesk.support |
| `com.google.android.gms.measurement.AppMeasurementReceiver` | false | — |
| `com.google.firebase.iid.FirebaseInstanceIdReceiver` | true | com.google.android.c2dm.intent.RECEIVE |
| `io.getstream.chat.android.client.receivers.NotificationMessageReceiver` | false | io.getstream.chat.android.READ, io.getstream.chat.android.REPLY |
| `androidx.glance.appwidget.action.ActionCallbackBroadcastReceiver` | false | — |
| `androidx.glance.appwidget.UnmanagedSessionReceiver` | false | — |
| `androidx.glance.appwidget.MyPackageReplacedReceiver` | false | android.intent.action.MY_PACKAGE_REPLACED |
| `androidx.work.impl.utils.ForceStopRunnable.BroadcastReceiver` | false | — |
| `androidx.work.impl.background.systemalarm.ConstraintProxy.BatteryChargingProxy` | false | android.intent.action.ACTION_POWER_CONNECTED, android.intent.action.ACTION_POWER_DISCONNECTED |
| `androidx.work.impl.background.systemalarm.ConstraintProxy.BatteryNotLowProxy` | false | android.intent.action.BATTERY_OKAY, android.intent.action.BATTERY_LOW |
| `androidx.work.impl.background.systemalarm.ConstraintProxy.StorageNotLowProxy` | false | android.intent.action.DEVICE_STORAGE_LOW, android.intent.action.DEVICE_STORAGE_OK |
| `androidx.work.impl.background.systemalarm.ConstraintProxy.NetworkStateProxy` | false | android.net.conn.CONNECTIVITY_CHANGE |
| `androidx.work.impl.background.systemalarm.RescheduleReceiver` | false | android.intent.action.BOOT_COMPLETED, android.intent.action.TIME_SET, android.intent.action.TIMEZONE_CHANGED |
| `androidx.work.impl.background.systemalarm.ConstraintProxyUpdateReceiver` | false | androidx.work.impl.background.systemalarm.UpdateProxies |
| `androidx.work.impl.diagnostics.DiagnosticsReceiver` | true | androidx.work.diagnostics.REQUEST_DIAGNOSTICS |
| `com.facebook.CurrentAccessTokenExpirationBroadcastReceiver` | false | com.facebook.sdk.ACTION_CURRENT_ACCESS_TOKEN_CHANGED |
| `com.facebook.AuthenticationTokenManager.CurrentAuthenticationTokenChangedBroadcastReceiver` | false | com.facebook.sdk.ACTION_CURRENT_AUTHENTICATION_TOKEN_CHANGED |
| `com.mapbox.common.location.LocationUpdatesReceiver` | false | com.mapbox.common.location.LocationUpdatesReceiver.ACTION_PROCESS_LOCATION_UPDATES |
| `androidx.profileinstaller.ProfileInstallReceiver` | true | androidx.profileinstaller.action.INSTALL_PROFILE, androidx.profileinstaller.action.SKIP_FILE, androidx.profileinstaller.action.SAVE_PROFILE, androidx.profileinstaller.action.BENCHMARK_OPERATION |
| `com.google.android.datatransport.runtime.scheduling.jobscheduling.AlarmManagerSchedulerBroadcastReceiver` | false | — |

---

## 6. Content providers (16)

| Provider | exported | authority |
|---|---|---|
| `androidx.startup.InitializationProvider` | false | `com.strava.androidx-startup` |
| `androidx.core.content.FileProvider` | false | `@string/export_fileprovider_name` |
| `com.strava.profiling.StravaPerfProvider` | false | `@string/perf_provider_authority` |
| `com.strava.authorization.sharedidentity.StravaIdentityProvider` | true | `com.strava.identity` |
| `com.strava.sharinginterface.SharingFileProvider` | false | `@string/sharing_fileprovider_name` |
| `zendesk.support.SupportSdkStartupProvider` | false | `com.strava.zendesk.support.SupportSdkStartupProvider` |
| `com.google.firebase.provider.FirebaseInitProvider` | false | `com.strava.firebaseinitprovider` |
| `io.getstream.chat.android.ui.common.StreamFileProvider` | false | `com.strava.streamfileprovider` |
| `io.getstream.android.push.delegate.AndroidPushDelegateProvider` | false | `com.strava.io.getstream.android.push` |
| `zendesk.support.guide.GuideSdkStartupProvider` | false | `com.strava.zendesk.support.GuideSdkStartupProvider` |
| `org.jetbrains.compose.resources.AndroidContextProvider` | false | `com.strava.resources.AndroidContextProvider` |
| `io.sentry.android.core.SentryInitProvider` | false | `com.strava.SentryInitProvider` |
| `io.sentry.android.core.SentryPerformanceProvider` | false | `com.strava.SentryPerformanceProvider` |
| `com.facebook.internal.FacebookInitProvider` | false | `com.strava.FacebookInitProvider` |
| `zendesk.core.MediaFileProvider` | false | `com.strava.zendesk.sdk.user.attachments` |
| `com.squareup.picasso.PicassoProvider` | false | `com.strava.com.squareup.picasso` |

---

## 7. `<meta-data>` (56)

| Key | Value |
|---|---|
| `io.sentry.dsn` | `https://<SENTRY_PUBLIC_KEY>@o352714.ingest.sentry.io/<SENTRY_PROJECT_ID>` |
| `android.max_aspect` | `2.5` |
| `com.google.android.gms.version` | `@integer/google_play_services_version` |
| `asset_statements` | `@string/asset_statements` |
| `com.google.android.geo.API_KEY` | `<GOOGLE_MAPS_API_KEY>` |
| `com.facebook.sdk.ApplicationId` | `fb284597785309` |
| `com.facebook.sdk.ClientToken` | `<FACEBOOK_CLIENT_TOKEN>` |
| `com.facebook.sdk.AutoLogAppEventsEnabled` | `false` |
| `firebase_analytics_collection_enabled` | `false` |
| `google_analytics_default_allow_analytics_storage` | `false` |
| `google_analytics_default_allow_ad_storage` | `false` |
| `google_analytics_default_allow_ad_user_data` | `false` |
| `google_analytics_default_allow_ad_personalization_signals` | `false` |
| `io.sentry.environment` | `production` |
| `io.sentry.auto-init` | `false` |
| `io.sentry.feedback.use-sentry-user` | `true` |
| `com.google.gms.fitness.sdk.version` | `21.1.0` |
| `com.google.android.play.billingclient.version` | `7.0.0` |
| `hostAppId` | `com.strava` |
| `io.sentry.gradle-plugin-integrations` | `AppStartInstrumentation,ComposeInstrumentation,DatabaseInstrumentation,FileIOInstrumentation,LogcatInstrumentation` |
| `com.android.vending.splits.required` | `true` |
| `com.android.stamp.source` | `https://play.google.com/store` |
| `com.android.stamp.type` | `STAMP_TYPE_DISTRIBUTION_APK` |
| `com.android.vending.splits` | `@xml/splits0` |
| `com.android.vending.derived.apk.id` | `4` |
