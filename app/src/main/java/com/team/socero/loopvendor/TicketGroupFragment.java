package com.team.socero.loopvendor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by Mahrus Kazi on 2018-06-12.
 */

public class TicketGroupFragment extends Fragment {

    EditText ticketName;
    Spinner ticketType;
    EditText ticketPrice;
    EditText ticketQuantity;
    EditText minTicketOrder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.frag_ticket_group, container, false);

        ticketName = v.findViewById(R.id.ticket_name_edit);
        ticketType = v.findViewById(R.id.ticket_type_spinner);
        ticketPrice = v.findViewById(R.id.ticket_price_edit);
        ticketQuantity = v.findViewById(R.id.ticket_quantity_edit);
        minTicketOrder = v.findViewById(R.id.ticket_min_order_edit);

        return v;
    }

    public String ticketName(){
        return ticketName.getText().toString();
    }

    public double ticketPrice(){
        return Double.parseDouble(ticketPrice.getText().toString());
    }

    public int ticketQuantity(){
        return Integer.parseInt(ticketQuantity.getText().toString());
    }

    public int minTicketOrder(){
        return Integer.parseInt(minTicketOrder.getText().toString());
    }
}
