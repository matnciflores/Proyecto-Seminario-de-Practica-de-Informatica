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
        this.checkIn = new Date(); //asigna la fecha/hora actual
        this.reserva.getCabaña().marcarOcupada();
    }

    public void registrarCheckOut() {
        this.checkOut = new Date(); 
        this.reserva.getCabaña().marcarDisponible();
        this.reserva.finalizarReserva();
    }

    public Date getCheckIn() {
        return checkIn; 
    }
    
    public void setCheckIn(Date checkIn) { 
        this.checkIn = checkIn; 
    }
    
    public Date getCheckOut() { 
        return checkOut; 
    }
    
    public void setCheckOut(Date checkOut) { 
        this.checkOut = checkOut; 
    }
    
    public Reserva getReserva() { 
        return reserva; 
    }
}