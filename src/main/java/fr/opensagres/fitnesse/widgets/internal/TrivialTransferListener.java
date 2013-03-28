package fr.opensagres.fitnesse.widgets.internal;

import org.sonatype.aether.transfer.TransferCancelledException;
import org.sonatype.aether.transfer.TransferEvent;
import org.sonatype.aether.transfer.TransferListener;

public class TrivialTransferListener implements TransferListener {


	@Override
	public void transferCorrupted(TransferEvent event) throws TransferCancelledException {

	}

	@Override
	public void transferFailed(TransferEvent event) {
		System.err.println(event);

	}

	@Override
	public void transferInitiated(TransferEvent event) throws TransferCancelledException {
		// TODO Auto-generated method stub

	}

	@Override
	public void transferProgressed(TransferEvent event) throws TransferCancelledException {
		// TODO Auto-generated method stub

	}

	@Override
	public void transferStarted(TransferEvent event) throws TransferCancelledException {
		System.out.println("transferStarted " + event);

	}

	@Override
	public void transferSucceeded(TransferEvent event) {
		System.out.println("transferSucceeded " + event);

	}

}
