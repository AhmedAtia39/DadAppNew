package net.senior.dadapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**

 */
public class HomeFragment extends Fragment {

    View v;
    RecyclerView rec;
    FolderAdapter folderAdapter;
    FoldersModel foldersModel;
    List<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment, container, false);
        rec=v.findViewById(R.id.rec);
        rec.setLayoutManager(new GridLayoutManager(getContext(),3));
        if(getArguments()!=null){
            list= (List<String>) getArguments().get("list");
        }else{
            list=new ArrayList();
        }
        folderAdapter=new FolderAdapter(list);
        folderAdapter.setOnImgClicked(new FolderAdapter.OnImgClicked() {
            @Override
            public void onImgClicked(int pos) {
                Intent i=new Intent(getActivity(),ImagesActivity.class);
                i.putExtra("name",list.get(pos));
startActivity(i);
            }
        });
        rec.setAdapter(folderAdapter);

        return v;
    }
}