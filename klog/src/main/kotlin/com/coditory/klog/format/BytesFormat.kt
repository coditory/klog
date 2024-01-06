package com.coditory.klog.format

object BytesFormat {
    fun formatBytesSI(bytes: Long): String {
        return QuantityFormat.formatQuantitySIWithSuffix(bytes, 'B')
    }

    fun formatBytesBin(bytes: Long): String {
        return QuantityFormat.formatQuantityBinWithSuffix(bytes, 'B')
    }
}
