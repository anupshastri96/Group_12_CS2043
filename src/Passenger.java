package src;

public class Passenger {
    private int id;
    private Stop boardedStop;
    private Stop departedStop;
    private PaymentMethod paymentMethod;
 
    public Passenger(int id, Stop boardedStop, Stop departedStop, PaymentMethod paymentMethod) {
        this.id = id;
        this.boardedStop = boardedStop;
        this.departedStop = departedStop;
        this.paymentMethod = paymentMethod;
    }
 
    public int getId() {
        return id;
    }
 
    public Stop getBoardedStop() {
        return boardedStop;
    }
 
    public void setBoardedStop(Stop newBoardedStop) {
        this.boardedStop = newBoardedStop;
    }
 
    public Stop getDepartedStop() {
        return departedStop;
    }
 
    public void setDeparted(Stop newDepartedStop) {
        this.departedStop = newDepartedStop;
    }
 
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}
