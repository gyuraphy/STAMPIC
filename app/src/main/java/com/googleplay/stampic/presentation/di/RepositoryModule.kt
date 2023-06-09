package com.googleplay.stampic.presentation.di

import com.googleplay.stampic.data.remote.datasource.*
import com.googleplay.stampic.data.repository.*
import com.googleplay.stampic.domain.repository.*

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @OtherHttpClient
    @Provides
    @Singleton
    fun bindsPlaceRepository(@OtherHttpClient placeRemoteDataSource: PlaceRemoteDataSource): PlaceRepository {
        return PlaceRepositoryImpl(placeRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideSignInRepository(authRemoteDataSource: AuthRemoteDataSource): AuthRepository {
        return AuthRepositoryImpl(authRemoteDataSource)
    }

    @Provides
    @Singleton
    fun provideSignUpRepository(signUpDataSource: SignUpDataSource): SignUpRepository {
        return SignUpRepositoryImpl(signUpDataSource)
    }

    @Provides
    @Singleton
    fun provideHomeRepository(homeDataSource: HomeDataSource): HomeRepository {
        return HomeRepositoryImpl(homeDataSource)
    }

//    @Provides
//    @Singleton
//    fun provideDetailRepository(detailDataSource: DetailDataSource): DetailRepository {
//        return DetailRepositoryImpl(detailDataSource)
//    }

//    @Provides
//    @Singleton
//    fun providePopupBottomRepository(popupBottomDataSource: PopupBottomDataSource): PopupBottomRepository {
//        return PopupBottomRepositoryImpl(popupBottomDataSource)
//    }

//    @Provides
//    @Singleton
//    fun provideSearchRepository(searchDataSource: SearchDataSource): SearchRepository {
//        return SearchRepositoryImpl(searchDataSource)
//    }

//    @Provides
//    @Singleton
//    fun provideGroupRepository(groupDataSource: GroupDataSource): GroupRepository {
//        return GroupRepositoryImpl(groupDataSource)
//    }

//    @Provides
//    @Singleton
//    fun providePostRepository(postDataSource: PostDataSource): PostRepository {
//        return PostRepositoryImpl(postDataSource)
//    }

//    @Provides
//    @Singleton
//    fun provideAlertRepository(alertDataSource: AlertDataSource): AlertRepository {
//        return AlertRepositoryImpl(alertDataSource)
//    }

//    @Provides
//    @Singleton
//    fun provideMissionRepository(missionDataSource: MissionDataSource): MissionRepository {
//        return MissionRepositoryImpl(missionDataSource)
//    }

//    @Provides
//    @Singleton
//    fun provideMapRepository(mapDataSource: MapDataSource): MapRepository {
//        return MapRepositoryImpl(mapDataSource)
//    }

    @Provides
    @Singleton
    fun provideMyPageRepository(myPageDataSource: MyPageDataSource): MyPageRepository {
        return MyPageRepositoryImpl(myPageDataSource)
    }
}