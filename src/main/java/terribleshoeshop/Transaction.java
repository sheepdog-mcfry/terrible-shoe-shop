package terribleshoeshop;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.math.BigDecimal;
import java.net.URI;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


/**
 * Created by Waseem on 06/05/16.
 */
public class Transaction {

    public enum State {
        STARTED, FAILED, COMPLETED
    }
    private String orderId;
    private String purchaseDescription;
    private String amount;
    private Product product;
    private URI successRedirect;
    private URI cancelRedirect;
    private String merchantName;
    private String connectId;
    private String vatRate;

    @JsonCreator
    private static Transaction createTransaction(
            @JsonProperty("orderId") final String orderId,
            @JsonProperty("purchaseDescription") final String purchaseDescription,
            @JsonProperty("amount") final String amount,
            @JsonProperty("vatRate") final String vatRate,
            @JsonProperty("product") final Product product,
            @JsonProperty("successRedirect") final URI successRedirect,
            @JsonProperty("cancelRedirect") final URI cancelRedirect,
            @JsonProperty("merchantname") final String merchantName,
            @JsonProperty("connectId") final String connectId
            ) { return new Builder()
            .orderId(orderId)
            .purchaseDescription(purchaseDescription)
            .vatRate(vatRate)
            .amount(amount)
            .product(product)
            .successRedirect(successRedirect)
            .cancelRedirect(cancelRedirect)
            .merchantName(merchantName)
            .connectId(connectId)
            .build();
    }

    public static class Builder {

        private String amount;
        private String orderId;
        private String purchaseDescription;
        private URI successRedirect;
        private URI cancelRedirect;
        private String vatRate;
        private String merchantName;
        private String connectId;
        private Product product;
        private Builder() {}

        public Builder amount(final String amount) {
            this.amount = amount;
            return this;
        }

        public Builder vatRate(final String vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        public Builder product(final Product product) {
            this.product = product;
            return this;
        }

        public Builder orderId(final String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder purchaseDescription(final String purchaseDescription) {
            this.purchaseDescription = purchaseDescription;
            return this;
        }

        public Builder successRedirect(final URI successRedirect) {
            this.successRedirect = successRedirect;
            return this;
        }

        public Builder cancelRedirect(final URI cancelRedirect) {
            this.cancelRedirect = cancelRedirect;
            return this;
        }

        public Builder merchantName(final String merchantName) {
            this.merchantName = merchantName;
            return this;
        }

        public Builder connectId(final String connectId) {
            this.connectId = connectId;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

    private Transaction(final Builder transactionBuilder) {
        this.amount = transactionBuilder.amount;
        this.product = transactionBuilder.product;
        this.orderId = transactionBuilder.orderId;
        this.vatRate = transactionBuilder.vatRate;
        this.purchaseDescription = transactionBuilder.purchaseDescription;
        this.successRedirect = transactionBuilder.successRedirect;
        this.cancelRedirect = transactionBuilder.cancelRedirect;
        this.merchantName = transactionBuilder.merchantName;
        this.connectId = transactionBuilder.connectId;
    }

}


