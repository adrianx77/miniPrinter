#include "com_wn518_printer_core_PrinterLib.h"
#include <memory.h>
#ifdef __cplusplus
extern "C" {
#endif
void intcolor2bitcolorline(int * intcolor,int w,signed char * bitcolor,signed char * header,int headerSize);

JNIEXPORT  jbyteArray JNICALL Java_com_wn518_printer_core_PrinterLib_getBitmapData(JNIEnv *env, jobject thiz,jintArray bitdata,jint w,jint h)
{
	jint length = env->GetArrayLength(bitdata);
	jint* inbits = env->GetIntArrayElements(bitdata,0);

	jsize lineSize =w/8 + ((w%8)?1:0);
	jsize outBitsCount = lineSize*h;
	jbyteArray outArray = env->NewByteArray(outBitsCount);
	jbyte* outbits = env->GetByteArrayElements(outArray,0);

	for(jint j=0;j<h;j++)
	{
		intcolor2bitcolorline(inbits+j*w,w,outbits+lineSize*j,NULL,0);
	}

	return outArray;
}

JNIEXPORT  jbyteArray JNICALL Java_com_wn518_printer_core_PrinterLib_getBitmapDataWithHeader(JNIEnv *env, jobject thiz, jintArray bitdata,jint w,jint h,jbyteArray header)
{
	jint length = env->GetArrayLength(bitdata);
	jint* inbits = env->GetIntArrayElements(bitdata,0);

	jint   headerSize = env->GetArrayLength(header);
	jbyte* headerbits = env->GetByteArrayElements(header,0);

	jsize lineSize =w/8 + ((w%8)?1:0);
	jsize outBitsCount = lineSize*h + headerSize;
	jbyteArray outArray = env->NewByteArray(outBitsCount);
	jbyte* outbits = env->GetByteArrayElements(outArray,0);

	memcpy(outbits,headerbits,headerSize);
	for(jint j=0;j<h;j++)
	{
		intcolor2bitcolorline(inbits+j*w,w,outbits+headerSize+lineSize*j,0,0);
	}

	return outArray;
}

JNIEXPORT  jbyteArray JNICALL Java_com_wn518_printer_core_PrinterLib_getBitmapDataWithLineHeader(JNIEnv *env, jobject thiz, jintArray bitdata,jint w,jint h,jbyteArray lineHeader)
{
	jint length = env->GetArrayLength(bitdata);
	jint* inbits = env->GetIntArrayElements(bitdata,0);

	jint   headerSize = env->GetArrayLength(lineHeader);
	jbyte* header = env->GetByteArrayElements(lineHeader,0);

	jsize lineSize =w/8 + ((w%8)?1:0) + headerSize;
	jsize outBitsCount = lineSize*h;
	jbyteArray outArray = env->NewByteArray(outBitsCount);
	jbyte* outbits = env->GetByteArrayElements(outArray,0);

	for(jint j=0;j<h;j++)
	{
		intcolor2bitcolorline(inbits+j*w,w,outbits+lineSize*j,header,headerSize);
	}

	return outArray;
}
void intcolor2bitcolorline(int * intcolor,int w,signed char * bitcolor,signed char * header,int headerSize)
{
	int value = 0;
	int shiftCount = 7;
	int bitIndex=0;
	if(header!=0)
	{
		memcpy(bitcolor,header,headerSize);
	}

	for(int i=0;i<w;i++)
	{
		int color = intcolor[i];
		int r = (color >> 16) & 0xff;
		int g = (color >> 8) & 0xff;
		int b = color & 0xff;

		if (r <= 160 || g <= 160 || b <= 160)
			value |= 1<<shiftCount;

		shiftCount--;
		if(shiftCount<0)
		{
			shiftCount=7;
			bitcolor[bitIndex+headerSize] = value;
			value = 0;
			bitIndex++;
		}
	}
	if(w%8)
	{
		bitcolor[bitIndex+headerSize] = value;
	}
}
#ifdef __cplusplus
}
#endif
