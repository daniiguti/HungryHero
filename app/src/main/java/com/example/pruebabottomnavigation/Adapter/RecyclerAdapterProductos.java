package com.example.pruebabottomnavigation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.pruebabottomnavigation.Modelos.Producto;
import com.example.pruebabottomnavigation.R;

import java.util.ArrayList;

public class RecyclerAdapterProductos extends RecyclerView.Adapter<RecyclerAdapterProductos.RecyclerHolder>{
    //Atributos de nuestra clase
    private ArrayList<Producto> listProductos;
    private CircularProgressDrawable progressDrawable;
    private Context context;
    private String tipo;
    private View.OnClickListener listener;

    //Constructor
    public RecyclerAdapterProductos(ArrayList<Producto> listProductos, Context context){
        this.listProductos = listProductos;
        this.context = context;
    }

    //Setter del listener
    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    //Esto "infla" cada celda del recyclerView con nuestro diseño
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.producto,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);
        view.setOnClickListener(listener);
        return recyclerHolder;
    }

    //Esto junta cada Libro del arrayList con el diseño de cada celda
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        Producto producto = this.listProductos.get(position);
        holder.tvNombre.setText(producto.getNombre());
        holder.tvPrecio.setText(producto.getPrecio() + "€");

        progressDrawable = new CircularProgressDrawable(context);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setStyle(CircularProgressDrawable.LARGE);
        progressDrawable.setCenterRadius(30f);
        progressDrawable.start();

        if(producto.getImgURL().equals("no tiene")){                 //Comprobamos que tiene imagen, sino la tiene ponemos una
            holder.imagenProducto.setImageResource(R.drawable.noimage);       //una por defecto nosotros (para cuando añada un producto)
        }else{
            Glide.with(context)
                    .load(producto.getImgURL())
                    .placeholder(progressDrawable)
                    .error(R.drawable.not_found)
                    .into(holder.imagenProducto);
        }

    }

    @Override
    public int getItemCount() {
        return listProductos.size();
    }

    //Enlazamos los elementos del diseño en relacion a nuestra clase
    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvPrecio;
        ImageView imagenProducto;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = (TextView) itemView.findViewById(R.id.tvNombre);
            tvPrecio = (TextView) itemView.findViewById(R.id.tvInfoFamilia);
            imagenProducto = (ImageView) itemView.findViewById(R.id.imgFamilia);
        }
    }


    //Para insertar en nuestro arrayList
    public void insertar(Producto producto){
        this.listProductos.add(producto);
        this.notifyDataSetChanged();
    }
    //Para eliminar un elemento de nuestro arraylist
    public void eliminar(int pos){
        this.listProductos.remove(pos);
        this.notifyDataSetChanged();
    }
    //Para borrar nuestro arrayList
    public void clear(){
        this.listProductos.clear();
        this.notifyDataSetChanged();
    }
    //Para devolver de nuestro arrayList
    public Producto devolverProducto(int posicion){
        return this.listProductos.get(posicion);
    }

}
