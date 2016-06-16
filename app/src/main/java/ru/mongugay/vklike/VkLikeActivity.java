package ru.mongugay.vklike;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKPostArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.mongugay.vklike.adapters.VLAdapter;
import ru.mongugay.vklike.adapters.VLListAdapter;
import ru.mongugay.vklike.adapters.VLListPostAdapter;
import ru.mongugay.vklike.models.IVLModel;
import ru.mongugay.vklike.models.VLApiModel;
import ru.mongugay.vklike.models.VLApiPhoto;
import ru.mongugay.vklike.models.VLApiPost;
import ru.mongugay.vklike.models.VLFaveModel;

public class VkLikeActivity extends Activity {

    private Button loginBtn;
    private Button getUSerInfoBtn;
    private TextView textView;
    private VKRequest request;
    private Button _getLikePhotoBtn;
    private Button _getLikePostBtn;
    private ListView _listPhotos;
    private VLFaveModel model;
    private VLListAdapter adapter;
    private VLListPostAdapter postsAdapter;
    private ArrayList<VLApiModel> photos;
    private ArrayList<VLApiModel> posts;
    private Button _deleteSelectedBtn;
    private Boolean _isTypePhoto = false;
    private VLAdapter _currentAdapter = null;

    private static final String[] sMyScope = new String[]{
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.MESSAGES,
            VKScope.DOCS
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vk_like);

        loginBtn           = (Button) findViewById(R.id.loginBtn);
        //getUSerInfoBtn     = (Button) findViewById(R.id.getUserInfoBtn);
        _getLikePhotoBtn   = (Button) findViewById(R.id.getLikePhoto);
        _deleteSelectedBtn = (Button) findViewById(R.id.deleteSelected);
        _getLikePostBtn    = (Button) findViewById(R.id.getLikePostBtn);

        textView    = (TextView) findViewById(R.id.textView);
        _listPhotos = (ListView) findViewById(R.id.listView);

        photos  = new ArrayList<VLApiModel>();
        posts   = new ArrayList<VLApiModel>();

        adapter      = new VLListAdapter(this, photos);
        postsAdapter = new VLListPostAdapter(this, posts);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onLoginClick();
            }
        });
        /*getUSerInfoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onGetUserInfo();
            }
        });*/
        _getLikePhotoBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onGetLikePhoto();
            }
        });
        _deleteSelectedBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onDeleteSelected();
            }
        });
        _getLikePostBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onGetLikePost();
            }
        });
    }

    /**
     * Авторизация
     */
    private void onLoginClick()
    {
        VKSdk.login(this, sMyScope);
    }

    /**
     * Кол-во лайков на постах
     */
    private void onGetLikePost()
    {
        _isTypePhoto = false;
        if (_currentAdapter != postsAdapter) {
            _currentAdapter = postsAdapter;
            _listPhotos.setAdapter(_currentAdapter);
        }
        VKParameters params = new VKParameters();
        params.put(VKApiConst.COUNT, 100);
        //params.put(VKApiConst.OFFSET, 1700);
        VKRequest onGetLikePostsRequest = new VKRequest("fave.getPosts", params);
        onGetLikePostsRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                JSONObject r = null;
                try {
                    r = response.json.getJSONObject("response");
                    model = new VLFaveModel();

                    model.count = r.getString("count");
                    model.items = r.getJSONArray("items");

                    ArrayList<VLApiPost> postsArr = new ArrayList<VLApiPost>();

                    VKPostArray vkPostArr = new VKPostArray();
                    vkPostArr.parse(r);

                    VLApiPost item;
                    for (int i = 0; i < vkPostArr.size(); ++i) {
                        item = new VLApiPost(vkPostArr.get(i));
                        postsArr.add(item);
                    }

                    posts.clear();
                    posts.addAll(postsArr);
                    postsAdapter.notifyDataSetChanged();

                    textView.setText(r.getString("count"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
            }
        });
    }

    /**
     * Кол-во лайков на фото
     */
    private void onGetLikePhoto()
    {
        _isTypePhoto = true;
        if (_currentAdapter != adapter) {
            _currentAdapter = adapter;
            _listPhotos.setAdapter(_currentAdapter);
        }
        VKParameters params = new VKParameters();
        params.put(VKApiConst.COUNT, 100);
        //params.put(VKApiConst.OFFSET, 40);
        VKRequest onGetLikePhotoRequest = new VKRequest("fave.getPhotos", params);
        onGetLikePhotoRequest.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                JSONObject r = null;
                try {
                    r = response.json.getJSONObject("response");
                    model = new VLFaveModel();

                    model.count = r.getString("count");
                    model.items = r.getJSONArray("items");

                    VKPhotoArray vkPhotoArr = new VKPhotoArray();
                    vkPhotoArr.parse(r);

                    ArrayList<VLApiPhoto> photoArr = new ArrayList<VLApiPhoto>();

                    VLApiPhoto item;
                    for (int i = 0; i < vkPhotoArr.size(); ++i) {
                        item = new VLApiPhoto(vkPhotoArr.get(i));
                        photoArr.add(item);
                    }

                    photos.clear();
                    photos.addAll(photoArr);
                    adapter.notifyDataSetChanged();

                    textView.setText(r.getString("count"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("vkLog", "onGetLikePhoto onComplete: " + response.responseString);
            }

            private void setInfo(ArrayList<VLApiPhoto> arr) {
                //photos.clear();
                photos = new ArrayList<VLApiModel>(arr);
                // photos = arr.clone();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
            }
        });
    }

    private ArrayList _selectedItems;
    private void onDeleteSelected()
    {
        _selectedItems = showResult();
        deleteSelected();
    }

    private IVLModel _model = null;
    /**
     * Удаляем выбранное
     */
    private void deleteSelected()
    {
        if (_selectedItems.size() != 0)
        {
            _model = (IVLModel) _selectedItems.get(0);
        }

        if (_selectedItems.size() != 0)
        {
            VKParameters params = new VKParameters();
            String type = _isTypePhoto ? VKApiConst.PHOTO : "post";
            params.put("type", type);
            params.put(VKApiConst.OWNER_ID, _model.getOwnerId());
            params.put("item_id", _model.getId());

            VKRequest onGetLikePhotoRequest = new VKRequest("likes.delete", params);
            onGetLikePhotoRequest.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    Log.d("vkLog", "onDeleteSelected: " + response.responseString);
                    removeCheckedFromArray();
                    _selectedItems.remove(0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            deleteSelected();
                        }
                    }, 500);
                }

                @Override
                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                    super.attemptFailed(request, attemptNumber, totalAttempts);
                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                    textView.setText(error.toString());
                    //Log.d("vkLog", "onDeleteSelected error: " + error.toString());
                }

                @Override
                public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                    super.onProgress(progressType, bytesLoaded, bytesTotal);
                }
            });
        }
        else
        {
            if (_isTypePhoto) {
                onGetLikePhoto();
            } else onGetLikePost();
        }
    }

    private void removeCheckedFromArray()
    {
        IVLModel p;

        ArrayList<VLApiModel> arr = _isTypePhoto ? photos : posts;
        for (int i = 0; i < arr.size(); ++i)
        {
            p = arr.get(i);
            if (p.getId() == _model.getId())
            {
                arr.remove(i);
                i--;
            }
        }
        _currentAdapter.notifyDataSetChanged();
    }

    // выводим информацию о корзине
    private ArrayList showResult() {
        ArrayList checkedItems = new ArrayList();

        for (VLApiModel p : _currentAdapter.getBox()) {
            if (p.box) {
                checkedItems.add(p);
            }
        }
        return checkedItems;
    }

    /**
     * Инфо пользователя
     */
    private void onGetUserInfo() {
        request = VKApi.users().get();
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                //Do complete stuff
                Log.d("vkLog", response.responseString);
            }

            @Override
            public void onError(VKError error) {
                //Do error stuff
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                //I don't really believe in progress
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
            // Пользователь успешно авторизовался
            }
            @Override
            public void onError(VKError error) {
            // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vk_like, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
