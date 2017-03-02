/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.microsoft.embeddedsocial.fetcher.base.FetchableAdapter;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.ui.adapter.renderer.Renderer;

/**
 * Base class for adapters dealing with multiple view types, delegates the rendering routine to {@link Renderer}'s.
 *
 * @param <T>  common type of objects to render
 * @param <VH> common type of view holders
 */
public abstract class MultiTypeAdapter<T, VH extends RecyclerView.ViewHolder> extends FetchableAdapter<T, VH> {

	private final SparseArray<ViewType<?, ? extends VH>> viewTypes = new SparseArray<>();

	protected MultiTypeAdapter(Fetcher<T> fetcher) {
		super(fetcher);
	}

	/**
	 * Register a renderer for the view type.
	 *
	 * @param viewType     int code for the view type, unique in the adapter
	 * @param renderer     renderer
	 * @param getter       object responsible for getting objects for rendering by its position, can be null
	 * @param <ObjectType> type of objects to render for this view type
	 */
	protected <ObjectType> void registerViewType(int viewType, Renderer<? super ObjectType, ? extends VH> renderer, GetMethod<? extends ObjectType> getter) {
		viewTypes.append(viewType, new ViewType<>(renderer, getter));
	}

	/**
	 * Register a renderer for the view type. {@link #getItem(int)} method will be used to get objects to render.
	 *
	 * @param viewType int code for the view type, unique in the adapter
	 * @param renderer renderer
	 */
	protected void registerViewType(int viewType, Renderer<? super T, ? extends VH> renderer) {
		viewTypes.append(viewType, new ViewType<>(renderer, this::getItem));
	}

	@Override
	public VH onCreateViewHolder(ViewGroup parent, int viewType) {
		return viewTypes.get(viewType).createViewHolder(parent);
	}

	@Override
	public void onBindViewHolder(VH holder, int position) {
		int viewType = holder.getItemViewType();
		viewTypes.get(viewType).render(holder, position);
	}

	/**
	 * Encapsulates an information about view type.
	 *
	 * @param <ObjectType>     type of objects to render
	 * @param <ViewHolderType> type of view holder
	 */
	private static class ViewType<ObjectType, ViewHolderType extends RecyclerView.ViewHolder> {
		private final Renderer<? super ObjectType, ViewHolderType> renderer;
		private final GetMethod<? extends ObjectType> getter;

		ViewType(Renderer<? super ObjectType, ViewHolderType> renderer, GetMethod<? extends ObjectType> getter) {
			this.renderer = renderer;
			this.getter = getter;
		}

		void render(RecyclerView.ViewHolder holder, int position) {
			renderer.renderItem(getter.get(position), holder);
		}

		ViewHolderType createViewHolder(ViewGroup parent) {
			return renderer.createViewHolder(parent);
		}
	}

	/**
	 * Object responsible for getting an object for rendering by its position.
	 *
	 * @param <ObjectType> type of objects.
	 */
	public interface GetMethod<ObjectType> {
		ObjectType get(int position);
	}

	public static <T> GetMethod<T> dummyGetMethod() {
		return position -> null;
	}

}
