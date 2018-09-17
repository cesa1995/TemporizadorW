package com.example.cesar.temporizadorw;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.example.cesar.temporizadorw.tabs.ajustes;
import com.example.cesar.temporizadorw.tabs.humedad;
import com.example.cesar.temporizadorw.tabs.temperatura;
import com.example.cesar.temporizadorw.tabs.temporizador;

import java.lang.ref.WeakReference;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final SparseArray<WeakReference<Fragment>> instantiatedFragments = new SparseArray<>();


    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                temporizador temporizador = new temporizador();
                return temporizador;
            case 1:
                humedad humedad = new humedad();
                return humedad;
            case 2:
                temperatura temperatura = new temperatura();
                return temperatura;
            case 3:
                ajustes ajustes = new ajustes();
                return ajustes;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        instantiatedFragments.put(position, new WeakReference<>(fragment));
        return fragment;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        instantiatedFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    @Nullable
    public Fragment getFragment(final int position) {
        final WeakReference<Fragment> wr = instantiatedFragments.get(position);
        if (wr != null) {
            return wr.get();
        } else {
            return null;
        }
    }
}


