package HooYah.Gateway.ticket.memory.bit;

import java.util.BitSet;

public class BitManager {

    private final BitSet bitSet;

    public BitManager(int size) {
        this.bitSet = new BitSet(size);
    }

    /**
     * index의 bit 값을 설정함
     * @Param index : 설장할 값의 index
     * @Param value : 값 true = 1, false = 0
     */
    public void set(int index, boolean value) {
        bitSet.set(index, value);
    }

    /**
     * index의 bit 값을 반환
     * @Param index : 읽을 값의 index
     */
    public boolean read(int index) {
        return bitSet.get(index);
    }

    /**
     * 특정 범위 [start, end] 내에서 1(true)인 비트의 개수를 반환
     * @Param start : include start index
     * @Param end : include end index
     */
    public int count(int start, int end) {
        return bitSet.get(start, end+1).cardinality();
    }

}