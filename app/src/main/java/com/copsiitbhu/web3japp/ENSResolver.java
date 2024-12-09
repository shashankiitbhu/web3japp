package com.copsiitbhu.web3japp;

import org.web3j.crypto.WalletUtils;
import org.web3j.ens.EnsResolver;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class ENSResolver {

    public static String resolveEnsName(String ensName, String infuraUrl) throws Exception {

        Web3j web3j = Web3j.build(new HttpService(infuraUrl));


        if (!isValidEnsName(ensName)) {
            throw new IllegalArgumentException("Invalid ENS Name: " + ensName);
        }


        EnsResolver ensResolver = new EnsResolver(web3j);


        String resolvedAddress = ensResolver.resolve(ensName);


        return resolvedAddress;
    }


    private static boolean isValidEnsName(String ensName) {
        return ensName != null && ensName.contains(".") && !ensName.startsWith(".");
    }
}
