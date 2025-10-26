package model;

import java.util.Date;

public class Estadia {

    private Date checkIn;
    private Date checkOut;
    
    private Reserva reserva; 

    public Estadia(Reserva reserva) {
        this.reserva = reserva;
    }

    public void registrarCheckIn() {
        this.checkIn = new Date(); //fecha/hora actual
        this.reserva.getCabaña().marcarOcupada();
    }

    //registra la fecha y hora actual como check-out
 
    public void registrarCheckOut() {
        this.checkOut = new Date(); //asigna la fecha/hora actual
        this.reserva.getCabaña().marcarDisponible();
        this.reserva.finalizarReserva();
    }

    public Date getCheckIn() {
        return checkIn;
    }

    public Date getCheckOut() {
        return checkOut;
    }
}