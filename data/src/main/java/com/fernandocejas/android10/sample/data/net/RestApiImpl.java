/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
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
package com.fernandocejas.android10.sample.data.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.fernandocejas.android10.sample.data.entity.UserEntity;
import com.fernandocejas.android10.sample.data.entity.mapper.UserEntityJsonMapper;
import com.fernandocejas.android10.sample.data.exception.NetworkConnectionException;
import io.reactivex.Observable;
import java.net.MalformedURLException;
import java.util.List;

/**
 * {@link RestApi} implementation for retrieving data from the network.
 */
public class RestApiImpl implements RestApi {

  private final Context context;
  private final UserEntityJsonMapper userEntityJsonMapper;
  private final String[] usersJson = {
          new String("[{\"id\":1,\"full_name\":\"Simon Hill\",\"followers\":7484},{\"id\":2,\"full_name\":\"Peter Graham\",\"followers\":7019},{\"id\":3,\"full_name\":\"Angelina Johnston\",\"followers\":2700},{\"id\":4,\"full_name\":\"Josh Hunt\",\"followers\":3322},{\"id\":5,\"full_name\":\"Victor Wallace\",\"followers\":5664},{\"id\":6,\"full_name\":\"Lorena Bishop\",\"followers\":4838},{\"id\":7,\"full_name\":\"Jack Daniels\",\"followers\":3945},{\"id\":8,\"full_name\":\"Montgomery Burns\",\"followers\":327},{\"id\":9,\"full_name\":\"Scott Matt\",\"followers\":3442},{\"id\":10,\"full_name\":\"John Sanchez\",\"followers\":4523},{\"id\":11,\"full_name\":\"Roger Marshall\",\"followers\":5134},{\"id\":12,\"full_name\":\"Pedro Garcia\",\"followers\":1381}]"),
          new String("{\"id\":1,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_1.jpg\",\"full_name\":\"Simon Hill\",\"description\":\"Curabitur gravida nisi at nibh. In hac habitasse platea dictumst. Aliquam augue quam, sollicitudin vitae, consectetuer eget, rutrum at, lorem.\\n\\nInteger tincidunt ante vel ipsum. Praesent blandit lacinia erat. Vestibulum sed magna at nunc commodo placerat.\\n\\nPraesent blandit. Nam nulla. Integer pede justo, lacinia eget, tincidunt eget, tempus vel, pede.\",\"followers\":7484,\"email\":\"jcooper@babbleset.edu\"}"),
          new String("{\"id\":2,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_2.jpg\",\"full_name\":\"Peter Graham\",\"description\":\"Aenean lectus. Pellentesque eget nunc. Donec quis orci eget orci vehicula condimentum.\\n\\nCurabitur in libero ut massa volutpat convallis. Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo. Maecenas pulvinar lobortis est.\",\"followers\":7019,\"email\":\"jward@shuffledrive.mil\"}"),
          new String("{\"id\":3,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_3.jpg\",\"full_name\":\"Angelina Johnston\",\"description\":\"Morbi non lectus. Aliquam sit amet diam in magna bibendum imperdiet. Nullam orci pede, venenatis non, sodales sed, tincidunt eu, felis.\\n\\nFusce posuere felis sed lacus. Morbi sem mauris, laoreet ut, rhoncus aliquet, pulvinar sed, nisl. Nunc rhoncus dui vel sem.\",\"followers\":2700,\"email\":\"dmendoza@zoomzone.gov\"}"),
          new String("{\"id\":4,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_2.jpg\",\"full_name\":\"Josh Hunt\",\"description\":\"Proin eu mi. Nulla ac enim. In tempor, turpis nec euismod scelerisque, quam turpis adipiscing lorem, vitae mattis nibh ligula nec sem.\\n\\nDuis aliquam convallis nunc. Proin at turpis a pede posuere nonummy. Integer non velit.\",\"followers\":3322,\"email\":\"wwood@rhynyx.edu\"}"),
          new String("{\"id\":5,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_1.jpg\",\"full_name\":\"Victor Wallace\",\"description\":\"Phasellus sit amet erat. Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.\\n\\nProin eu mi. Nulla ac enim. In tempor, turpis nec euismod scelerisque, quam turpis adipiscing lorem, vitae mattis nibh ligula nec sem.\\n\\nDuis aliquam convallis nunc. Proin at turpis a pede posuere nonummy. Integer non velit.\",\"followers\":5664,\"email\":\"whenderson@voomm.info\"}"),
          new String("{\"id\":6,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_3.jpg\",\"full_name\":\"Lorena Bishop\",\"description\":\"Praesent id massa id nisl venenatis lacinia. Aenean sit amet justo. Morbi ut odio.\\n\\nCras mi pede, malesuada in, imperdiet et, commodo vulputate, justo. In blandit ultrices enim. Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\\n\\nProin interdum mauris non ligula pellentesque ultrices. Phasellus id sapien in sapien iaculis congue. Vivamus metus arcu, adipiscing molestie, hendrerit at, vulputate vitae, nisl.\\n\\nAenean lectus. Pellentesque eget nunc. Donec quis orci eget orci vehicula condimentum.\",\"followers\":4838,\"email\":\"ggordon@livefish.org\"}"),
          new String("{\"id\":7,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_1.jpg\",\"full_name\":\"Jack Daniels\",\"description\":\"Pellentesque at nulla. Suspendisse potenti. Cras in purus eu magna vulputate luctus.\\n\\nCum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Vivamus vestibulum sagittis sapien. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\",\"followers\":3945,\"email\":\"tjohnson@zoonoodle.biz\"}"),
          new String("{\"id\":8,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_2.jpg\",\"full_name\":\"Montgomery Burns\",\"description\":\"Duis consequat dui nec nisi volutpat eleifend. Donec ut dolor. Morbi vel lectus in quam fringilla rhoncus.\\n\\nMauris enim leo, rhoncus sed, vestibulum sit amet, cursus id, turpis. Integer aliquet, massa id lobortis convallis, tortor risus dapibus augue, vel accumsan tellus nisi eu orci. Mauris lacinia sapien quis libero.\\n\\nNullam sit amet turpis elementum ligula vehicula consequat. Morbi a ipsum. Integer a nibh.\\n\\nIn quis justo. Maecenas rhoncus aliquam lacus. Morbi quis tortor id nulla ultrices aliquet.\\n\\nMaecenas leo odio, condimentum id, luctus nec, molestie sed, justo. Pellentesque viverra pede ac diam. Cras pellentesque volutpat dui.\",\"followers\":327,\"email\":\"jharvey@meezzy.name\"}"),
          new String("{\"id\":9,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_3.jpg\",\"full_name\":\"Scott Matt\",\"description\":\"Morbi porttitor lorem id ligula. Suspendisse ornare consequat lectus. In est risus, auctor sed, tristique in, tempus sit amet, sem.\\n\\nFusce consequat. Nulla nisl. Nunc nisl.\\n\\nDuis bibendum, felis sed interdum venenatis, turpis enim blandit mi, in porttitor pede justo eu massa. Donec dapibus. Duis at velit eu est congue elementum.\",\"followers\":3442,\"email\":\"wwelch@yodo.info\"}"),
          new String("{\"id\":10,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_1.jpg\",\"full_name\":\"John Sanchez\",\"description\":\"Curabitur gravida nisi at nibh. In hac habitasse platea dictumst. Aliquam augue quam, sollicitudin vitae, consectetuer eget, rutrum at, lorem.\\n\\nInteger tincidunt ante vel ipsum. Praesent blandit lacinia erat. Vestibulum sed magna at nunc commodo placerat.\\n\\nPraesent blandit. Nam nulla. Integer pede justo, lacinia eget, tincidunt eget, tempus vel, pede.\\n\\nMorbi porttitor lorem id ligula. Suspendisse ornare consequat lectus. In est risus, auctor sed, tristique in, tempus sit amet, sem.\",\"followers\":4523,\"email\":\"dmedina@katz.edu\"}"),
          new String("{\"id\":11,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_2.jpg\",\"full_name\":\"Roger Marshall\",\"description\":\"Curabitur in libero ut massa volutpat convallis. Morbi odio odio, elementum eu, interdum eu, tincidunt in, leo. Maecenas pulvinar lobortis est.\\n\\nPhasellus sit amet erat. Nulla tempus. Vivamus in felis eu sapien cursus vestibulum.\\n\\nProin eu mi. Nulla ac enim. In tempor, turpis nec euismod scelerisque, quam turpis adipiscing lorem, vitae mattis nibh ligula nec sem.\\n\\nDuis aliquam convallis nunc. Proin at turpis a pede posuere nonummy. Integer non velit.\",\"followers\":5134,\"email\":\"bnguyen@roombo.mil\"}"),
          new String("{\"id\":12,\"cover_url\":\"https://raw.githubusercontent.com/android10/Sample-Data/master/Android-CleanArchitecture/cover_3.jpg\",\"full_name\":\"Pedro Garcia\",\"description\":\"Maecenas ut massa quis augue luctus tincidunt. Nulla mollis molestie lorem. Quisque ut erat.\\n\\nCurabitur gravida nisi at nibh. In hac habitasse platea dictumst. Aliquam augue quam, sollicitudin vitae, consectetuer eget, rutrum at, lorem.\\n\\nInteger tincidunt ante vel ipsum. Praesent blandit lacinia erat. Vestibulum sed magna at nunc commodo placerat.\",\"followers\":1381,\"email\":\"wmoreno@skipstorm.net\"}"),
  };

  /**
   * Constructor of the class
   *
   * @param context {@link android.content.Context}.
   * @param userEntityJsonMapper {@link UserEntityJsonMapper}.
   */
  public RestApiImpl(Context context, UserEntityJsonMapper userEntityJsonMapper) {
    if (context == null || userEntityJsonMapper == null) {
      throw new IllegalArgumentException("The constructor parameters cannot be null!!!");
    }
    this.context = context.getApplicationContext();
    this.userEntityJsonMapper = userEntityJsonMapper;
  }

  @Override public Observable<List<UserEntity>> userEntityList() {
    return Observable.create(emitter -> {
      if (isThereInternetConnection()) {
        try {
          String responseUserEntities = getUserEntitiesFromApi();
          if (responseUserEntities != null) {
            emitter.onNext(userEntityJsonMapper.transformUserEntityCollection(
                responseUserEntities));
            emitter.onComplete();
          } else {
            emitter.onError(new NetworkConnectionException());
          }
        } catch (Exception e) {
          emitter.onError(new NetworkConnectionException(e.getCause()));
        }
      } else {
        emitter.onError(new NetworkConnectionException());
      }
    });
  }

  @Override public Observable<UserEntity> userEntityById(final int userId) {
    return Observable.create(emitter -> {
      if (isThereInternetConnection()) {
        try {
          String responseUserDetails = getUserDetailsFromApi(userId);
          if (responseUserDetails != null) {
            emitter.onNext(userEntityJsonMapper.transformUserEntity(responseUserDetails));
            emitter.onComplete();
          } else {
            emitter.onError(new NetworkConnectionException());
          }
        } catch (Exception e) {
          emitter.onError(new NetworkConnectionException(e.getCause()));
        }
      } else {
        emitter.onError(new NetworkConnectionException());
      }
    });
  }

  private String getUserEntitiesFromApi() {
    return usersJson[0];
  }

  private String getUserDetailsFromApi(int userId) {
    return usersJson[userId];
  }

  /**
   * Checks if the device has any active internet connection.
   *
   * @return true device with internet connection, otherwise false.
   */
  private boolean isThereInternetConnection() {
    boolean isConnected;

    ConnectivityManager connectivityManager =
        (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
    isConnected = (networkInfo != null && networkInfo.isConnectedOrConnecting());

    return isConnected;
  }
}
