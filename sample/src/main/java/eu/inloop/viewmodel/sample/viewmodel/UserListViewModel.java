package eu.inloop.viewmodel.sample.viewmodel;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import eu.inloop.viewmodel.AbstractViewModel;
import eu.inloop.viewmodel.sample.viewmodel.view.IUserListView;

public class UserListViewModel extends AbstractViewModel<IUserListView> {

    private static final int TOTAL_USERS = 7;
    private List<String> mLoadedUsers;

    //Don't persist state variables
    private boolean mLoadingUsers;

    private float mCurrentLoadingProgress = 0;

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        //this will be only not null in case the application was killed due to low memory
        if (savedInstanceState != null) {
            mLoadedUsers = savedInstanceState.getStringArrayList("userlist");
        }
    }

    @Override
    public void onBindView(@NonNull IUserListView view) {
        super.onBindView(view);

        //downloading list of users
        if (mLoadedUsers != null) {
            view.showUsers(mLoadedUsers);
        } else if (mLoadingUsers) {
            view.showLoading(mCurrentLoadingProgress);
        } else {
            loadUsers();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loadUsers() {
        mLoadingUsers = true;
        mCurrentLoadingProgress = 0;
        getViewOptional().showLoading(mCurrentLoadingProgress);
        new AsyncTask<Void, Float, List<String>>() {

            @Override
            protected List<String> doInBackground(Void... voids) {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < TOTAL_USERS; i++) {
                    list.add("User " + i);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        //
                    }
                    publishProgress((i+1) / (float)TOTAL_USERS);
                }

                return list;
            }

            @Override
            protected void onProgressUpdate(Float... values) {
                super.onProgressUpdate(values);
                mCurrentLoadingProgress = values[0];
                getViewOptional().showLoading(mCurrentLoadingProgress);
            }

            @Override
            protected void onPostExecute(List<String> s) {
                super.onPostExecute(s);
                mLoadedUsers = s;
                mLoadingUsers = false;
                getViewOptional().showUsers(s);
                getViewOptional().hideProgress();
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteUser(final int position) {
        if (position > mLoadedUsers.size() - 1) {
            return;
        }
        mLoadedUsers.set(position, "Deleting in 5 seconds...");
        getViewOptional().showUsers(mLoadedUsers);

        final String itemToDelete = mLoadedUsers.get(position);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                   //
                }
                mLoadedUsers.remove(itemToDelete);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                getViewOptional().showUsers(mLoadedUsers);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void onSaveInstanceState(@NonNull final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (mLoadedUsers != null) {
            bundle.putStringArrayList("userlist", new ArrayList<>(mLoadedUsers));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //use this to cancel any planned requests
    }
}
