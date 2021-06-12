import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@RunWith(JUnit4::class)
internal class MerkleTreeTest {

    @Test
    fun initializeMerkleTree() {
        val classLoader = javaClass.classLoader
        val testFile = classLoader.getResource("test.txt").path

        val tree = MerkleTree(testFile)
        assertNotNull(tree)
    }

    @Test
    fun initWithNonExistentFile() {
        assertFailsWith<IOException> {
            val tree = MerkleTree("non existent")
        }
    }

    @Test
    fun initWithFolder() {
        val classLoader = javaClass.classLoader
        val testFolder = classLoader.getResource("folder").path

        assertFailsWith<IOException> {
            MerkleTree(testFolder)
        }
    }

    @Test
    fun checkSingleBlockFile() {
        val classLoader = javaClass.classLoader
        val testFile = classLoader.getResource("test.txt").path

        val tree = MerkleTree(testFile)
        assertEquals("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3", tree.rootHash)
    }

    @Test
    fun checkTwoBlockFile() {
        val classLoader = javaClass.classLoader
        val testFile = classLoader.getResource("64k.txt").path

        val tree = MerkleTree(testFile)
        assertEquals("55b6950b101bd55ebbf8c4f39ab0d648eb517dd657023e7f3e0769567f59f8db", tree.rootHash)
    }

    @Test
    fun checkThreeBlockFile() {
        val classLoader = javaClass.classLoader
        val testFile = classLoader.getResource("96k.txt").path

        val tree = MerkleTree(testFile)
        assertEquals("90be445a0ab1f7dd2703b27a9cfb8abc8d1234ad257ca3782c782635acbd4290", tree.rootHash)
    }

    @Test
    fun invalidInitialize1() {
        assertFailsWith<Exception> {
            val tree = MerkleTree()
        }
    }

    @Test
    fun invalidInitialize2() {
        assertFailsWith<Exception> {
            val tree = MerkleTree("file.txt", ByteArray(32))
        }
    }
}