import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

class MerkleTree(fileName: String, private val blockSize: Int = 32 * 1024) {
    private val fileHandle: File = File(fileName)
    private val messageDigest: MessageDigest by lazy {
        MessageDigest.getInstance("SHA-256")
    }

    private val tree: HashNode

    val rootHash: String get() {
        return String.format("%064x", BigInteger(1, tree.hash))
    }

    init {
        require(fileHandle.exists()) {
            throw IOException("File does not exist")
        }

        require(!fileHandle.isDirectory) {
            throw IOException("Input should be a directory")
        }

        tree = constructTree()
    }

    private fun constructTree(): HashNode {
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