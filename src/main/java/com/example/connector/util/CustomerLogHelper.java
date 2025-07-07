package com.example.connector.util;

import org.slf4j.Logger;

public class CustomerLogHelper {
    public enum ActionType {
        FETCH_CUSTOMERS,
        CREATE_CUSTOMERS,
        DELETE_CUSTOMERS,
        UPDATE_CUSTOMERS,
        PARSE_NS_ERROR
    }


    public enum Status {
        SUCCESS,
        FAIL
    }

    public enum SourceType {
        NS,
        DB
    }

    public static void logCustomerAction(Logger logger, String custId,
                                         String firstname, String lastname,
                                         ActionType actionType,
                                         SourceType source,
                                         Status status, String error,
                                         boolean isError) {
        if (isError) {
            logger.error("custId={}, firstname={}, lastname={}, action={}, source={}, status={}, error={}",
                    custId, firstname, lastname, actionType, source,
                    status,
                    error);
        } else {
            logger.info("custId={}, firstname={}, lastname={}, action={}, source={}, status={}, error={}",
                    custId, firstname, lastname, actionType, source, status, error);
        }
    }
}
