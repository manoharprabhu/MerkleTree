import kotlinx.serialization.cbor.Cbor
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class MerkleTree(
    fileName: String? = null,
    treeData: ByteArray? = null,
    private val blockSize: Int = 32 * 1024) {

    private val messageDigest: MessageDigest by lazy {
        MessageDigest.getInstance("SHA-256")
    }

    private val tree: HashNode

    val rootHash: String get() {
        return String.format("%064x", BigInteger(1, tree.hash))
    }

    private fun serialize(): ByteArray {
        return Cbor.encodeToByteArray(HashNode.serializer(), tree)
    }

    fun serializeToBase64(): String {
        return Base64.getEncoder().encodeToString(serialize())
    }

    init {
        // fileName or treeData mutually exclusive
        if(!((fileName != null) xor (treeData != null))) {
            throw Exception("Either fileName or treeData is required for initialization, but not both")
        }

        tree = if(fileName != null) {
            val fileHandle = File(fileName)

            require(fileHandle.exists()) { "File does not exist" }
            require(!fileHandle.isDirectory) { "Input should be a directory" }

            constructTree(fileHandle)
        } else {
            Cbor.decodeFromByteArray(HashNode.serializer(), treeData!!)
        }
    }

    private fun constructTree(fileHandle: File): HashNode {
        val blockHashes = mutableListOf<ByteArray>()
        val block = ByteArray(blockSize)
        fileHandle.inputStream().buffered().use { input ->
            while(true) {
                val read = input.read(block)
                if(read <= 0) {
                    break
                }
                if(read == blockSize) {
                    blockHashes.add(messageDigest.digest(block))
                } else {
                    blockHashes.add(messageDigest.digest(block.copyOf(read)))
                }
            }
        }

        return computeRootHash(blockHashes)
    }

    private fun computeRootHash(blockHashes: List<ByteArray>): HashNode {
        return computeRootHashHelper(blockHashes, 0, blockHashes.size - 1)
    }

    private fun computeRootHashHelper(blockHashes: List<ByteArray>, left: Int, right: Int): HashNode {
        if(left == right) {
            return HashNode(blockHashes[left], left, right, null, null)
        }

        val mid = left + (right - left) / 2
        val leftNode = computeRootHashHelper(blockHashes, left, mid)
        val rightNode = computeRootHashHelper(blockHashes, mid + 1, right)
        return HashNode(sha256(leftNode.hash + rightNode.hash), left, right, leftNode, rightNode)
    }

    private fun sha256(byteArray: ByteArray): ByteArray {
        return messageDigest.digest(byteArray)
    }
}