package com.googleplay.stampic.presentation.di


import com.googleplay.stampic.BuildConfig
import com.googleplay.stampic.data.api.*
import com.googleplay.stampic.data.remote.datasource.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @OtherHttpClient
    @Provides
    @Singleton
    fun providePlaceDataSource(@OtherHttpClient placeService: PlaceService): PlaceRemoteDataSource {
        return PlaceRemoteDataSourceImpl(placeService)
    }

    @Provides
    @Singleton
    fun provideAuthDataSource(authService: AuthService): AuthRemoteDataSource {
        return AuthRemoteDataSourceImpl(authService)
    }

    @Provides
    @Singleton
    fun provideSignUpDataSource(signUpService: SignUpService): SignUpDataSource {
        return SignUpDataSourceImpl(signUpService)
    }

    @Provides
    @Singleton
    fun provideHomeDataSource(homeService: HomeService): HomeDataSource {
        return HomeDataSourceImpl(homeService)
    }

//    @Provides
//    @Singleton
//    fun provideDetailDataSource(detailService: DetailService): DetailDataSource {
//        return DetailDataSourceImpl(detailService)
//    }

//    @Provides
//    @Singleton
//    fun providePopupBottomDataSource(popupBottomService: PopupBottomService): PopupBottomDataSource {
//        return PopupBottomDataSourceImpl(popupBottomService)
//    }

//    @Provides
//    @Singleton
//    fun provideSearchDataSource(searchService: SearchService): SearchDataSource {
//        return SearchDataSourceImpl(searchService)
//    }

//    @Provides
//    @Singleton
//    fun provideGroupDataSource(groupService: GroupService): GroupDataSource {
//        return GroupDataSourceImpl(groupService)
//    }

//    @Provides
//    @Singleton
//    fun providePostDataSource(postService: PostService): PostDataSource {
//        return PostDataSourceImpl(postService)
//    }

//    @Provides
//    @Singleton
//    fun provideAlertDataSource(alertService: AlertService): AlertDataSource {
//        return AlertDataSourceImpl(alertService)
//    }

//    @Provides
//    @Singleton
//    fun provideMissionDataSource(missionService: MissionService): MissionDataSource {
//        return MissionDataSourceImpl(missionService)
//    }

//    @Provides
//    @Singleton
//    fun provideMapDataSource(mapService: MapService): MapDataSource {
//        return MapDataSourceImpl(mapService)
//    }

    @Provides
    @Singleton
    fun provideMyPageDataSource(myPageService: MyPageService): MyPageDataSource {
        return MyPageDataSourceImpl(myPageService)
    }
}