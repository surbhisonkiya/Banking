package upm.ssa.bank;

import java.io.*;

public class OpsBank implements Serializable {

	private static final long serialVersionUID = 1L;
	private OpsEnum 	  ops;
	private Client        client        = null;
	private Integer       accountNumber = 0;
	private ClientDB      clientDB      = null;
	
   // ADD_CLIENT, UPDATE_CLIENT
	public OpsBank (OpsEnum ops,
			            Client client) {
		this.ops = ops;
		this.client    = client;
	}
	
	// READ_CLIENT, DELETE_CLIENT
	public OpsBank (OpsEnum ops,
						 Integer accountNumber ) {
		this.ops     = ops;
		this.accountNumber = accountNumber;
	}

	public OpsBank (OpsEnum ops, 
						 ClientDB clientDB) {
		this.ops = ops;
		this.clientDB  = clientDB;
	}

	
	public OpsEnum getOperation() {
		return ops;
	}

	public void setOperation(OpsEnum ops) {
		this.ops = ops;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Integer getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Integer accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	public ClientDB getClientDB() {
		return clientDB;
	}

	public void setClientDB(ClientDB clientDB) {
		this.clientDB = clientDB;
	}
	
	@Override
	public String toString() {
		
		String string = null;
		
		string = "OperationBank [ops=" + ops;
		if (client != null) string = string + ", client=" + client.toString();
		string = string + ", accountNumber=" + accountNumber + "]\n";
		if (clientDB != null) string = string + clientDB.toString();

		System.out.println("toString: " + string);
		
		return string;
	}

	public static byte[] objToByte(OpsBank opsBank) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
		objStream.writeObject(opsBank);

		return byteStream.toByteArray();
	}

	public static OpsBank byteToObj(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		ObjectInputStream objStream = new ObjectInputStream(byteStream);

		return (OpsBank) objStream.readObject();
	}
}
