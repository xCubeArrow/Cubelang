package com.cubearrow.cubelang.backend.instructionselection

import com.cubearrow.cubelang.backend.REGISTER_COUNT
import com.cubearrow.cubelang.common.ASMEmitter
import com.cubearrow.cubelang.common.ir.*

/**
 * A currently active lifetime interval of a specific register.
 *
 * @param regIndex The index of the currently active register
 */
data class CurrentActiveRegisterLiveInterval(
    val regIndex: Int,
    val start: Int,
    val end: Int
)

/**
 * An interval of the lifetime of a virtual register.
 */
data class VirtualRegisterLiveInterval(
    val virtualRegIndex: Int,
    val start: Int,
    var end: Int
)

/**
 * Assignment of real registers to the virtual registers given in the intermediate representation.
 *
 * @param emitter The emitter whose IR values are to be assigned registers
 */
class RegisterAllocation(private val emitter: ASMEmitter) {

    /**
     * Runs the linear scan register allocation algorithm on the current results of the [[emitter]].
     */
    fun linearScanRegisterAllocation() {
        val intervals = getLiveIntervals(emitter.resultIRValues)
        val freeRegisters = (0 until REGISTER_COUNT).reversed().toMutableList()
        val active = mutableListOf<CurrentActiveRegisterLiveInterval>()
        for (i in intervals) {
            expireOldIntervals(i, active, freeRegisters)
            if (active.size == REGISTER_COUNT)
                spillAtInterval(i)
            else {
                val regIndex = freeRegisters.removeLast()
                setAllocatedRegister(regIndex, i)
                active.add(CurrentActiveRegisterLiveInterval(regIndex, i.start, i.end))
            }
        }
    }

    private fun setAllocatedRegister(regIndex: Int, interval: VirtualRegisterLiveInterval) {
        for (i in interval.start..interval.end) {
            emitter.resultIRValues[i].arg0?.let { setAllocatedRegister(it, regIndex, interval.virtualRegIndex) }
            emitter.resultIRValues[i].arg1?.let { setAllocatedRegister(it, regIndex, interval.virtualRegIndex) }
        }
    }
    private fun setAllocatedRegister(irValue: ValueType, regIndex: Int, virtualRegIndex: Int){
        when(irValue){
            is TemporaryRegister -> if(irValue.index == virtualRegIndex) irValue.allocatedIndex = regIndex
            is RegOffset -> if(irValue.temporaryRegister.index == virtualRegIndex) irValue.temporaryRegister.allocatedIndex = regIndex
            is FramePointerOffset -> if(irValue.temporaryRegister != null && irValue.temporaryRegister.index == virtualRegIndex) irValue.temporaryRegister.allocatedIndex = regIndex
        }
    }

    private fun spillAtInterval(i: VirtualRegisterLiveInterval) {
        TODO("Not yet implemented")
    }

    private fun expireOldIntervals(i: VirtualRegisterLiveInterval, active: MutableList<CurrentActiveRegisterLiveInterval>, freeRegisters: MutableList<Int>) {
        for (j in active.sortedBy { it.end }) {
            if (j.end >= i.start)
                return
            active.remove(j)
            freeRegisters.add(j.regIndex)
        }
    }

    private fun getLiveIntervals(resultIRValues: List<IRValue>): List<VirtualRegisterLiveInterval> {
        val resultList = mutableListOf<VirtualRegisterLiveInterval>()
        for (i in resultIRValues.indices) {
            addLiveInterval(resultIRValues[i].arg0, resultList, i)
            addLiveInterval(resultIRValues[i].arg1, resultList, i)
        }
        resultList.sortBy { it.start }
        return resultList
    }

    private fun addLiveInterval(value: ValueType?, resultList: MutableList<VirtualRegisterLiveInterval>, index: Int) {
        value?.let {
            if (value is TemporaryRegister) {
                // Register not yet accounted for
                addSingularIndex(resultList, value, index)
            }
            if (value is RegOffset) {
                // Register not yet accounted for
                addSingularIndex(resultList, value.temporaryRegister, index)
            }
            if(value is FramePointerOffset && value.temporaryRegister != null){
                addSingularIndex(resultList, value.temporaryRegister, index)
            }
        }
    }

    private fun addSingularIndex(
        resultList: MutableList<VirtualRegisterLiveInterval>,
        value: TemporaryRegister,
        index: Int
    ) {
        if (resultList.none { it.virtualRegIndex == value.index }) {
            resultList.add(VirtualRegisterLiveInterval(value.index, index, index))
        } else {
            val indexOfFirst = resultList.indexOfFirst { it.virtualRegIndex == value.index }
            resultList[indexOfFirst].end = index
        }
    }
}
