import java.util.ArrayList;

public class TxHandler {

    /**
     * Creates a public ledger whose current UTXOPool (collection of unspent transaction outputs) is
     * {@code utxoPool}. This should make a copy of utxoPool by using the UTXOPool(UTXOPool uPool)
     * constructor.
     */
    private UTXOPool HandlerUtxoPool;

    public TxHandler(UTXOPool utxoPool) {
        // IMPLEMENT THIS
        HandlerUtxoPool = new UTXOPool(utxoPool);
    }

    /**
     * @return true if:
     * (1) all outputs claimed by {@code tx} are in the current UTXO pool, 
     * (2) the signatures on each input of {@code tx} are valid, 
     * (3) no UTXO is claimed multiple times by {@code tx},
     * (4) all of {@code tx}s output values are non-negative, and
     * (5) the sum of {@code tx}s input values is greater than or equal to the sum of its output
     *     values; and false otherwise.
     */
    public boolean isValidTx(Transaction tx) {
        // IMPLEMENT THIS
        ArrayList<Transaction.Output> OutputsArray = tx.getOutputs();
        ArrayList<Transaction.Input> InputsArray = tx.getInputs();
        UTXOArray = new ArrayList<UTXO>();
        double inputsum = 0;
        double outputsum = 0;

        //* (1) & (2) & (3)
        for (Transaction.Input Input : InputsArray) {
            utxo =  new UTXO(Input.prevTxHash, Input.outputIndex);

            //* (3) no UTXO is claimed multiple times by {@code tx},
            if (UTXOArray.contains(utxo)) {
                return false;
            }
            
            UTXOArray.add(utxo);
            //* (1)all outputs claimed by {@code tx} are in the current UTXO pool,
            if (!HandlerUtxoPool.contains(utxo)) {
                return false;
            }
            //* (2) the signatures on each input of {@code tx} are valid, 
            if (Crypto.verifySignature(HandlerUtxoPool.getTxOutput(utxo).address, tx.getRawTx(), Input.signature)){
                return false;
            }
            else {
                inputsum = inputsum + HandlerUtxoPool.getTxOutput(utxo).value;
            }
        }

        //* (4) & (5)
        for (Transaction.Output Output : OutputsArray) {
            //* (4) all of {@code tx}s output values are non-negative, and
            if (Output.value < 0) {
                return false;
            }           
            outputsum = outputsum + Output.value;       
        }

        //* the sum of {@code tx}s input values is greater than or equal to the sum of its output
        if (inputsum >= outputsum) {
            return false;
        }

        return true;
    }

    /**
     * Handles each epoch by receiving an unordered array of proposed transactions, checking each
     * transaction for correctness, returning a mutually valid array of accepted transactions, and
     * updating the current UTXO pool as appropriate.
     */
    public Transaction[] handleTxs(Transaction[] possibleTxs) {
        // IMPLEMENT THIS
    	acceptedTxs = new ArrayList<Transaction>();
    	for(Transaction tx : possibleTxs) {
    		if (isValidTx(tx)) {
    			acceptedTxs.add(tx);
    			for (int i = 0; i<tx.numInputs(); i++) {
    	            utxo =  new UTXO(Input.prevTxHash, Input.outputIndex);
    				HandlerUtxoPool.remove(utxo);
    			}
    			for (int i = 0; i<tx.numOutputs(); i++) {
    				utxo =  new UTXO(tx.getHash(), i);
    				HandlerUtxoPool.add(utxo, tx.getOutputs()[i]);
    			}
    			    			
    		}
    		Transaction[] txArray = acceptedTxs.toArray(new Transaction[acceptedTxs.size()]);
    	}
    	return txArray;
    }

}
