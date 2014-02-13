package turtlekit.cuda;

import static jcuda.driver.JCudaDriver.cuMemFreeHost;

import java.nio.IntBuffer;
import java.util.concurrent.ExecutionException;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import turtlekit.cuda.CudaEngine.Kernel;

public class CudaGPUGradientsPhero extends CudaPheromone implements CudaObject{
	
	private IntBuffer fieldMaxDir;
	
	protected CUdeviceptr fieldMaxDirPtr;
//	private Runnable fieldMinDirComputation;
	protected Pointer maxPinnedMemory;
//	private Runnable diffusionAndEvaporation;
	private Runnable diffusionUpdateAndEvaporationAndFieldMaxDir;

//	protected int[] fieldMaxDirAsArray;

	public CudaGPUGradientsPhero(String name, int width, int height, final int evapPercentage,
			final int diffPercentage) {
		super(name, width, height, evapPercentage, diffPercentage);
//		fieldMaxDirAsArray = new int[width * height];
	}
	
	public void initCuda() {
		super.initCuda();
		final int intGridMemorySize = getWidth() * getHeight() * Sizeof.INT;

		try {
			getCudaEngine().submit(new Runnable() {
				public void run() {
					fieldMaxDirPtr = new CUdeviceptr();
					maxPinnedMemory = new Pointer();
					fieldMaxDir = CudaEngine.getUnifiedIntBuffer(maxPinnedMemory,
							fieldMaxDirPtr, intGridMemorySize);
//					fieldMaxDirAsArray = CudaEngine.getUnifiedIntArray(minPinnedMemory, fieldMaxDirPtr, getWidth() * getHeight());
					initFunctions();
				}
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	protected void initFunctions() {
		super.initFunctions();
		final CUfunction diffusionUpdateAndEvaporationAndFieldMaxDirFunction = getCudaEngine().getKernelFunction(Kernel.DIFFUSION_UPDATE_THEN_EVAPORATION_THEN_FIELDMAXDIRV2);
		diffusionUpdateAndEvaporationAndFieldMaxDir = new Runnable() {
			@Override
			public void run() {
//				launchKernel(Pointer.to(
//						Pointer.to(widthParam),
//						Pointer.to(heightParam),
//						Pointer.to(getValuesPinnedMemory()),
//						Pointer.to(tmpPtr),
//						getPointerToFloat(getEvaporationCoefficient()),
////						Pointer.to(minPinnedMemory)
//						Pointer.to(fieldMaxDirPtr)
//						)
//						, diffusionUpdateAndEvaporationAndFieldMaxDirFunction);
				launchKernel(diffusionUpdateAndEvaporationAndFieldMaxDirFunction,
						Pointer.to(widthParam),
						Pointer.to(heightParam),
						Pointer.to(getValuesPinnedMemory()),
						Pointer.to(tmpPtr),
						getPointerToFloat(getEvaporationCoefficient()),
//						Pointer.to(minPinnedMemory)
						Pointer.to(fieldMaxDirPtr)
						);
			}
		};
		
//		final CUfunction fieldMaxDirFunction = getCudaEngine().getKernelFunction(Kernel.FIELD_MAX_DIR);
//		fieldMinDirComputation = new Runnable() {
//			@Override
//			public void run() {
//				Pointer kernelParameters = Pointer.to(
//						Pointer.to(widthParam),
//						Pointer.to(heightParam),
//						Pointer.to(valuesPtr),
//						Pointer.to(fieldMaxDirPtr)
//						);
//
//				launchKernel(kernelParameters, fieldMaxDirFunction);
//			}
//		};
}

	/**
	 * This is faster than calling them sequentially: 
	 * Only one GPU kernel is called.
	 * 
	 */
	@Override
	public void diffusionAndEvaporation() {
		getCudaEngine().submit(diffusionToTmp);
		getCudaEngine().submit(diffusionUpdateAndEvaporationAndFieldMaxDir);
	}
	
	@Override
	public int getMaxDirection(int i, int j) {
		return fieldMaxDir.get(get1DIndex(i, j));
//		return fieldMaxDirAsArray[get1DIndex(i, j)];
	}
	
//	public void updateFieldMaxDir() {
//			cuda.submit(fieldMinDirComputation);
//	}
	
	public void freeMemory() {
		super.freeMemory();
		getCudaEngine().submit(new Runnable() {
			@Override
			public void run() {
				cuMemFreeHost(maxPinnedMemory);
				cuMemFreeHost(fieldMaxDirPtr);
			}
		});
	}

}
