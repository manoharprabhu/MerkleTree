import java.io.File
import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest

class MerkleTree(fileName: String) {
    private val fileHandle: File = File(fileName)
    private val blockSize: Int = 32 * 1024 // 32 Kilobytes
    private val messageDigest: MessageDigest by lazy {
        MessageDigest.getInstance("SHA-256")
    }
    private val _rootHash: ByteArray

    val rootHash: String get() {
        return String.format("%064x", BigInteger(1, _rootHash))
    }

    init {
        require(fileHandle.exists()) {
            throw IOException("File does not exist")
        }

        require(!fileHandle.isDirectory) {
            throw IOException("Input should be a directory")
        }

        _rootHash = constructTree()
    }

    private fun constructTree(): ByteArray {
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

    private fun computeRootHash(blockHashes: List<ByteArray>): ByteArray {
        return computeRootHashHelper(blockHashes, 0, blockHashes.size - 1)
    }

    private fun computeRootHashHelper(blockHashes: List<ByteArray>, left: Int, right: Int): ByteArray {
        if(left == right) {
            return blockHashes[left]
        }

        val mid = left + (right - left) / 2
        val leftHash = computeRootHashHelper(blockHashes, left, mid)
        val rightHash = computeRootHashHelper(blockHashes, mid + 1, right)
        return sha256(leftHash + rightHash)
    }

    private fun sha256(byteArray: ByteArray): ByteArray {
        return messageDigest.digest(byteArray)
    }
}