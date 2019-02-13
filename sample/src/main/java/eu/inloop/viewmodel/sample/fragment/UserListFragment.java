package eu.inloop.viewmodel.sample.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.inloop.viewmodel.base.ViewModelBaseFragment;
import eu.inloop.viewmodel.sample.R;
import eu.inloop.viewmodel.sample.SampleApplication;
import eu.inloop.viewmodel.sample.activity.SampleBindingActivity;
import eu.inloop.viewmodel.sample.activity.ViewPagerActivity;
import eu.inloop.viewmodel.sample.viewmodel.UserListViewModel;
import eu.inloop.viewmodel.sample.viewmodel.view.IUserListView;

public class UserListFragment extends ViewModelBaseFragment<IUserListView, UserListViewModel> implements IUserListView {

    @BindView(android.R.id.progress)
    View mProgressView;
    @BindView(R.id.progress_text)
    TextView mProgressText;
    @BindView(android.R.id.list)
    ListView mListview;
    @BindView(R.id.open_binding_fragment)
    Button mOpenBindingFragment;

    private ArrayAdapter<String> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_userlist, container, false);
        ButterKnife.bind(this, view);

        final View headerView = inflater.inflate(R.layout.view_header_info, null, false);
        headerView.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireFragmentManager().beginTransaction().replace(R.id.root_content, SampleBundleFragment.newInstance(1234), "empty-fragment").addToBackStack(null).commit();
            }
        });
        headerView.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().finish();
                requireActivity().startActivity(requireActivity().getIntent());
            }
        });
        headerView.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), ViewPagerActivity.class));
            }
        });
        mListview.addHeaderView(headerView, null, false);
        mOpenBindingFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(SampleBindingActivity.newIntent(requireActivity()));
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getViewModel().deleteUser(i - mListview.getHeaderViewsCount());
            }
        });
        setModelView(this);
    }

    @Override
    public void showUsers(List<String> users) {
        mAdapter.setNotifyOnChange(false);
        mAdapter.clear();
        mAdapter.addAll(users);
        mAdapter.setNotifyOnChange(true);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading(float progress) {
        mProgressView.setVisibility(View.VISIBLE);
        mProgressText.setText((int) (progress * 100) + "%");
    }

    @Override
    public void hideProgress() {
        mProgressView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // watch for memory leaks
        RefWatcher refWatcher = SampleApplication.getRefWatcher(requireActivity());
        refWatcher.watch(this);
    }
}
