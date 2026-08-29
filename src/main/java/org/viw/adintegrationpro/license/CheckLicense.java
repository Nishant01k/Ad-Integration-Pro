package org.viw.adintegrationpro.license;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.ui.LicensingFacade;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.cert.*;
import java.util.*;

public class CheckLicense {

    /**
     * Must be exactly the same product code that is configured
     * in plugin.xml inside <product-descriptor>.
     */
    private static final String PRODUCT_CODE = "PADINTEGRATIONP";

    private static final String KEY_PREFIX = "key:";
    private static final String STAMP_PREFIX = "stamp:";

    /**
     * JetBrains public root certificates used for
     * verifying JetBrains-signed licenses.
     */
    private static final String[] ROOT_CERTIFICATES = new String[]{

            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIFOzCCAyOgAwIBAgIJANJssYOyg3nhMA0GCSqGSIb3DQEBCwUAMBgxFjAUBgNV\n" +
                    "BAMMDUpldFByb2ZpbGUgQ0EwHhcNMTUxMDAyMTEwMDU2WhcNNDUxMDI0MTEwMDU2\n" +
                    "WjAYMRYwFAYDVQQDDA1KZXRQcm9maWxlIENBMIICIjANBgkqhkiG9w0BAQEFAAOC\n" +
                    "Ag8AMIICCgKCAgEA0tQuEA8784NabB1+T2XBhpB+2P1qjewHiSajAV8dfIeWJOYG\n" +
                    "y+ShXiuedj8rL8VCdU+yH7Ux/6IvTcT3nwM/E/3rjJIgLnbZNerFm15Eez+XpWBl\n" +
                    "m5fDBJhEGhPc89Y31GpTzW0vCLmhJ44XwvYPntWxYISUrqeR3zoUQrCEp1C6mXNX\n" +
                    "EpqIGIVbJ6JVa/YI+pwbfuP51o0ZtF2rzvgfPzKtkpYQ7m7KgA8g8ktRXyNrz8bo\n" +
                    "iwg7RRPeqs4uL/RK8d2KLpgLqcAB9WDpcEQzPWegbDrFO1F3z4UVNH6hrMfOLGVA\n" +
                    "xoiQhNFhZj6RumBXlPS0rmCOCkUkWrDr3l6Z3spUVgoeea+QdX682j6t7JnakaOw\n" +
                    "jzwY777SrZoi9mFFpLVhfb4haq4IWyKSHR3/0BlWXgcgI6w6LXm+V+ZgLVDON52F\n" +
                    "LcxnfftaBJz2yclEwBohq38rYEpb+28+JBvHJYqcZRaldHYLjjmb8XXvf2MyFeXr\n" +
                    "SopYkdzCvzmiEJAewrEbPUaTllogUQmnv7Rv9sZ9jfdJ/cEn8e7GSGjHIbnjV2ZM\n" +
                    "Q9vTpWjvsT/cqatbxzdBo/iEg5i9yohOC9aBfpIHPXFw+fEj7VLvktxZY6qThYXR\n" +
                    "Rus1WErPgxDzVpNp+4gXovAYOxsZak5oTV74ynv1aQ93HSndGkKUE/qA/JECAwEA\n" +
                    "AaOBhzCBhDAdBgNVHQ4EFgQUo562SGdCEjZBvW3gubSgUouX8bMwSAYDVR0jBEEw\n" +
                    "P4AUo562SGdCEjZBvW3gubSgUouX8bOhHKQaMBgxFjAUBgNVBAMMDUpldFByb2Zp\n" +
                    "bGUgQ0GCCQDSbLGDsoN54TAMBgNVHRMEBTADAQH/MAsGA1UdDwQEAwIBBjANBgkq\n" +
                    "hkiG9w0BAQsFAAOCAgEAjrPAZ4xC7sNiSSqh69s3KJD3Ti4etaxcrSnD7r9rJYpK\n" +
                    "BMviCKZRKFbLv+iaF5JK5QWuWdlgA37ol7mLeoF7aIA9b60Ag2OpgRICRG79QY7o\n" +
                    "uLviF/yRMqm6yno7NYkGLd61e5Huu+BfT459MWG9RVkG/DY0sGfkyTHJS5xrjBV6\n" +
                    "hjLG0lf3orwqOlqSNRmhvn9sMzwAP3ILLM5VJC5jNF1zAk0jrqKz64vuA8PLJZlL\n" +
                    "S9TZJIYwdesCGfnN2AETvzf3qxLcGTF038zKOHUMnjZuFW1ba/12fDK5GJ4i5y+n\n" +
                    "fDWVZVUDYOPUixEZ1cwzmf9Tx3hR8tRjMWQmHixcNC8XEkVfztID5XeHtDeQ+uPk\n" +
                    "X+jTDXbRb+77BP6n41briXhm57AwUI3TqqJFvoiFyx5JvVWG3ZqlVaeU/U9e0gxn\n" +
                    "8qyR+ZA3BGbtUSDDs8LDnE67URzK+L+q0F2BC758lSPNB2qsJeQ63bYyzf0du3wB\n" +
                    "/gb2+xJijAvscU3KgNpkxfGklvJD/oDUIqZQAnNcHe7QEf8iG2WqaMJIyXZlW3me\n" +
                    "0rn+cgvxHPt6N4EBh5GgNZR4l0eaFEV+fxVsydOQYo1RIyFMXtafFBqQl6DDxujl\n" +
                    "FeU3FZ+Bcp12t7dlM4E0/sS1XdL47CfGVj4Bp+/VbF862HmkAbd7shs7sDQkHbU=\n" +
                    "-----END CERTIFICATE-----\n",

            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIFTDCCAzSgAwIBAgIJAMCrW9HV+hjZMA0GCSqGSIb3DQEBCwUAMB0xGzAZBgNV\n" +
                    "BAMMEkxpY2Vuc2UgU2VydmVycyBDQTAgFw0xNjEwMTIxNDMwNTRaGA8yMTE2MTIy\n" +
                    "NzE0MzA1NFowHTEbMBkGA1UEAwwSTGljZW5zZSBTZXJ2ZXJzIENBMIICIjANBgkq\n" +
                    "hkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAoT7LvHj3JKK2pgc5f02z+xEiJDcvlBi6\n" +
                    "fIwrg/504UaMx3xWXAE5CEPelFty+QPRJnTNnSxqKQQmg2s/5tMJpL9lzGwXaV7a\n" +
                    "rrcsEDbzV4el5mIXUnk77Bm/QVv48s63iQqUjVmvjQt9SWG2J7+h6X3ICRvF1sQB\n" +
                    "yeat/cO7tkpz1aXXbvbAws7/3dXLTgAZTAmBXWNEZHVUTcwSg2IziYxL8HRFOH0+\n" +
                    "GMBhHqa0ySmF1UTnTV4atIXrvjpABsoUvGxw+qOO2qnwe6ENEFWFz1a7pryVOHXg\n" +
                    "P+4JyPkI1hdAhAqT2kOKbTHvlXDMUaxAPlriOVw+vaIjIVlNHpBGhqTj1aqfJpLj\n" +
                    "qfDFcuqQSI4O1W5tVPRNFrjr74nDwLDZnOF+oSy4E1/WhL85FfP3IeQAIHdswNMJ\n" +
                    "y+RdkPZCfXzSUhBKRtiM+yjpIn5RBY+8z+9yeGocoxPf7l0or3YF4GUpud202zgy\n" +
                    "Y3sJqEsZksB750M0hx+vMMC9GD5nkzm9BykJS25hZOSsRNhX9InPWYYIi6mFm8QA\n" +
                    "2Dnv8wxAwt2tDNgqa0v/N8OxHglPcK/VO9kXrUBtwCIfZigO//N3hqzfRNbTv/ZO\n" +
                    "k9lArqGtcu1hSa78U4fuu7lIHi+u5rgXbB6HMVT3g5GQ1L9xxT1xad76k2EGEi3F\n" +
                    "9B+tSrvru70CAwEAAaOBjDCBiTAdBgNVHQ4EFgQUpsRiEz+uvh6TsQqurtwXMd4J\n" +
                    "8VEwTQYDVR0jBEYwRIAUpsRiEz+uvh6TsQqurtwXMd4J8VGhIaQfMB0xGzAZBgNV\n" +
                    "BAMMEkxpY2Vuc2UgU2VydmVycyBDQYIJAMCrW9HV+hjZMAwGA1UdEwQFMAMBAf8w\n" +
                    "CwYDVR0PBAQDAgEGMA0GCSqGSIb3DQEBCwUAA4ICAQCJ9+GQWvBS3zsgPB+1PCVc\n" +
                    "oG6FY87N6nb3ZgNTHrUMNYdo7FDeol2DSB4wh/6rsP9Z4FqVlpGkckB+QHCvqU+d\n" +
                    "rYPe6QWHIb1kE8ftTnwapj/ZaBtF80NWUfYBER/9c6To5moW63O7q6cmKgaGk6zv\n" +
                    "St2IhwNdTX0Q5cib9ytE4XROeVwPUn6RdU/+AVqSOspSMc1WQxkPVGRF7HPCoGhd\n" +
                    "vqebbYhpahiMWfClEuv1I37gJaRtsoNpx3f/jleoC/vDvXjAznfO497YTf/GgSM2\n" +
                    "LCnVtpPQQ2vQbOfTjaBYO2MpibQlYpbkbjkd5ZcO5U5PGrQpPFrWcylz7eUC3c05\n" +
                    "UVeygGIthsA/0hMCioYz4UjWTgi9NQLbhVkfmVQ5lCVxTotyBzoubh3FBz+wq2Qt\n" +
                    "iElsBrCMR7UwmIu79UYzmLGt3/gBdHxaImrT9SQ8uqzP5eit54LlGbvGekVdAL5l\n" +
                    "DFwPcSB1IKauXZvi1DwFGPeemcSAndy+Uoqw5XGRqE6jBxS7XVI7/4BSMDDRBz1u\n" +
                    "a+JMGZXS8yyYT+7HdsybfsZLvkVmc9zVSDI7/MjVPdk6h0sLn+vuPC1bIi5edoNy\n" +
                    "PdiG2uPH5eDO6INcisyPpLS4yFKliaO4Jjap7yzLU9pbItoWgCAYa2NpxuxHJ0tB\n" +
                    "7tlDFnvaRnQukqSG+VqNWg==\n" +
                    "-----END CERTIFICATE-----"
    };

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;

    /**
     * License server timestamps older than this are rejected.
     */
    private static final long TIMESTAMP_VALIDITY_PERIOD_MS = HOUR;

    /**
     * Checks whether Ad Integration Pro currently has
     * a valid JetBrains Marketplace license.
     *
     * @return TRUE  = valid license
     *         FALSE = no valid license
     *         NULL  = JetBrains LicensingFacade is not initialized yet
     */
    @Nullable
    public static Boolean isLicensed() {

        final LicensingFacade facade =
                LicensingFacade.getInstance();

        if (facade == null) {
            return null;
        }

        final String confirmationStamp =
                facade.getConfirmationStamp(PRODUCT_CODE);

        if (confirmationStamp == null) {
            return false;
        }

        /*
         * JetBrains Account / activation-code license.
         */
        if (confirmationStamp.startsWith(KEY_PREFIX)) {

            return isKeyValid(
                    confirmationStamp.substring(
                            KEY_PREFIX.length()
                    )
            );
        }

        /*
         * Floating License Server license.
         */
        if (confirmationStamp.startsWith(STAMP_PREFIX)) {

            return isLicenseServerStampValid(
                    confirmationStamp.substring(
                            STAMP_PREFIX.length()
                    )
            );
        }

        return false;
    }

    /**
     * Optional helper.
     *
     * Opens JetBrains' built-in license UI when it is
     * available in the current IDE.
     *
     * Your new Buy Pro browser flow does not depend on
     * this method, but keeping it gives us a fallback.
     */
    public static void requestLicense(
            final String message
    ) {

        ApplicationManager
                .getApplication()
                .invokeLater(
                        () -> showRegisterDialog(
                                PRODUCT_CODE,
                                message
                        ),
                        ModalityState.nonModal()
                );
    }

    /**
     * Attempts to open JetBrains' built-in registration /
     * license management action.
     */
    private static void showRegisterDialog(
            final String productCode,
            final String message
    ) {

        final ActionManager actionManager =
                ActionManager.getInstance();

        /*
         * Often available in open-source based products /
         * Android Studio.
         */
        AnAction registerAction =
                actionManager.getAction(
                        "RegisterPlugins"
                );

        /*
         * Commercial JetBrains IDE fallback.
         */
        if (registerAction == null) {

            registerAction =
                    actionManager.getAction(
                            "Register"
                    );
        }

        /*
         * Some IDE distributions may not expose either
         * registration action.
         *
         * In that situation we simply do nothing here.
         * The normal Buy Pro browser flow still works.
         */
        if (registerAction == null) {
            return;
        }

        registerAction.actionPerformed(
                AnActionEvent.createEvent(
                        asDataContext(
                                productCode,
                                message
                        ),
                        new Presentation(),
                        "",
                        ActionUiKind.NONE,
                        null
                )
        );
    }

    /**
     * Supplies the product code and optional message
     * to JetBrains' built-in registration action.
     */
    @NotNull
    private static DataContext asDataContext(
            final String productCode,
            @Nullable final String message
    ) {

        return dataId -> switch (dataId) {

            case "register.product-descriptor.code" ->
                    productCode;

            case "register.message" ->
                    message;

            default ->
                    null;
        };
    }

    /**
     * Validates JetBrains Marketplace activation keys.
     */
    private static boolean isKeyValid(
            final String key
    ) {

        final String[] licenseParts =
                key.split("-");

        if (licenseParts.length != 4) {
            return false;
        }

        final String licenseId =
                licenseParts[0];

        final String licensePartBase64 =
                licenseParts[1];

        final String signatureBase64 =
                licenseParts[2];

        final String certBase64 =
                licenseParts[3];

        try {

            final Signature signature =
                    Signature.getInstance(
                            "SHA1withRSA"
                    );

            /*
             * Certificate expiration checking is disabled
             * here because a license may also represent a
             * perpetual fallback license.
             */
            signature.initVerify(
                    createCertificate(
                            Base64
                                    .getMimeDecoder()
                                    .decode(
                                            certBase64.getBytes(
                                                    StandardCharsets.UTF_8
                                            )
                                    ),
                            Collections.emptySet(),
                            false
                    )
            );

            final byte[] licenseBytes =
                    Base64
                            .getMimeDecoder()
                            .decode(
                                    licensePartBase64.getBytes(
                                            StandardCharsets.UTF_8
                                    )
                            );

            signature.update(
                    licenseBytes
            );

            final boolean signatureValid =
                    signature.verify(
                            Base64
                                    .getMimeDecoder()
                                    .decode(
                                            signatureBase64.getBytes(
                                                    StandardCharsets.UTF_8
                                            )
                                    )
                    );

            if (!signatureValid) {
                return false;
            }

            /*
             * Verify that the license ID outside the signed
             * payload matches the license ID in the signed data.
             */
            final String licenseData =
                    new String(
                            licenseBytes,
                            StandardCharsets.UTF_8
                    );

            return licenseData.contains(
                    "\"licenseId\":\""
                            + licenseId
                            + "\""
            );

        } catch (Throwable ignored) {

            /*
             * Invalid / malformed license.
             */
            return false;
        }
    }

    /**
     * Validates a confirmation stamp received from a
     * JetBrains Floating License Server.
     */
    private static boolean isLicenseServerStampValid(
            final String serverStamp
    ) {

        try {

            final String[] parts =
                    serverStamp.split(":");

            /*
             * Required:
             * machineId
             * timestamp
             * actual machineId
             * signature algorithm
             * signature
             * certificate
             */
            if (parts.length < 6) {
                return false;
            }

            final Base64.Decoder base64 =
                    Base64.getMimeDecoder();

            final String expectedMachineId =
                    parts[0];

            final long timeStamp =
                    Long.parseLong(
                            parts[1]
                    );

            final String machineId =
                    parts[2];

            final String signatureType =
                    parts[3];

            final byte[] signatureBytes =
                    base64.decode(
                            parts[4].getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            final byte[] certBytes =
                    base64.decode(
                            parts[5].getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            final Collection<byte[]>
                    intermediateCertificates =
                    new ArrayList<>();

            for (
                    int index = 6;
                    index < parts.length;
                    index++
            ) {

                intermediateCertificates.add(
                        base64.decode(
                                parts[index].getBytes(
                                        StandardCharsets.UTF_8
                                )
                        )
                );
            }

            final Signature signature =
                    Signature.getInstance(
                            signatureType
                    );

            /*
             * Floating-license certificates are checked
             * against their current validity period.
             */
            signature.initVerify(
                    createCertificate(
                            certBytes,
                            intermediateCertificates,
                            true
                    )
            );

            signature.update(
                    (
                            timeStamp
                                    + ":"
                                    + machineId
                    ).getBytes(
                            StandardCharsets.UTF_8
                    )
            );

            if (!signature.verify(
                    signatureBytes
            )) {
                return false;
            }

            /*
             * Verify machine identity.
             */
            if (!expectedMachineId.equals(
                    machineId
            )) {
                return false;
            }

            /*
             * The Floating License Server stamp must
             * also be recent.
             */
            return Math.abs(
                    System.currentTimeMillis()
                            - timeStamp
            ) < TIMESTAMP_VALIDITY_PERIOD_MS;

        } catch (Throwable ignored) {

            return false;
        }
    }

    /**
     * Builds and validates the JetBrains certificate chain.
     */
    @NotNull
    private static X509Certificate createCertificate(
            final byte[] certBytes,
            final Collection<byte[]>
                    intermediateCertsBytes,
            final boolean
                    checkValidityAtCurrentDate
    ) throws Exception {

        final CertificateFactory factory =
                CertificateFactory.getInstance(
                        "X.509"
                );

        final X509Certificate certificate =
                (X509Certificate)
                        factory.generateCertificate(
                                new ByteArrayInputStream(
                                        certBytes
                                )
                        );

        final Collection<Certificate>
                allCertificates =
                new HashSet<>();

        allCertificates.add(
                certificate
        );

        for (
                byte[] bytes :
                intermediateCertsBytes
        ) {

            allCertificates.add(
                    factory.generateCertificate(
                            new ByteArrayInputStream(
                                    bytes
                            )
                    )
            );
        }

        try {

            final X509CertSelector selector =
                    new X509CertSelector();

            selector.setCertificate(
                    certificate
            );

            final Set<TrustAnchor>
                    trustAnchors =
                    new HashSet<>();

            for (
                    String rootCertificate :
                    ROOT_CERTIFICATES
            ) {

                final X509Certificate root =
                        (X509Certificate)
                                factory.generateCertificate(
                                        new ByteArrayInputStream(
                                                rootCertificate.getBytes(
                                                        StandardCharsets.UTF_8
                                                )
                                        )
                                );

                trustAnchors.add(
                        new TrustAnchor(
                                root,
                                null
                        )
                );
            }

            final PKIXBuilderParameters parameters =
                    new PKIXBuilderParameters(
                            trustAnchors,
                            selector
                    );

            parameters.setRevocationEnabled(
                    false
            );

            /*
             * For activation keys, validate the chain using
             * the certificate start date rather than today.
             *
             * This allows perpetual fallback licenses whose
             * signing certificate has since expired.
             */
            if (!checkValidityAtCurrentDate) {

                parameters.setDate(
                        certificate.getNotBefore()
                );
            }

            parameters.addCertStore(
                    CertStore.getInstance(
                            "Collection",
                            new CollectionCertStoreParameters(
                                    allCertificates
                            )
                    )
            );

            final CertPath certPath =
                    CertPathBuilder
                            .getInstance(
                                    "PKIX"
                            )
                            .build(
                                    parameters
                            )
                            .getCertPath();

            CertPathValidator
                    .getInstance(
                            "PKIX"
                    )
                    .validate(
                            certPath,
                            parameters
                    );

            return certificate;

        } catch (Exception exception) {

            throw new Exception(
                    "Certificate used to sign the license " +
                            "is not signed by a trusted " +
                            "JetBrains root certificate.",
                    exception
            );
        }
    }
}