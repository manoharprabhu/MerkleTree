import kotlinx.serialization.*

@Serializable
data class HashNode(
    val hash: ByteArray,
    val leftRange: Int,
    val rightRange: Int,
    val leftNode: HashNode?,
    val rightNode: HashNode?) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HashNode

        if (!hash.contentEquals(other.hash)) return false
        if (leftRange != other.leftRange) return false
        if (rightRange != other.rightRange) return false
        if (leftNode != other.leftNode) return false
        if (rightNode != other.rightNode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hash.contentHashCode()
        result = 31 * result + leftRange
        result = 31 * result + rightRange
        result = 31 * result + (leftNode?.hashCode() ?: 0)
        result = 31 * result + (rightNode?.hashCode() ?: 0)
        return result
    }
}