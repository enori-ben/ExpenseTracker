
package com.example.expensetracker.view.scan


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.EaseInQuart
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.expensetracker.BuildConfig
import com.example.expensetracker.R
import com.example.expensetracker.model.InvoiceResult
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.system.measureTimeMillis
import com.example.expensetracker.view.TransactionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Scanning(
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    viewModel: TransactionViewModel
) {
    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    BackHandler {
        onClose()
    }

    if (hasPermission) {
        Box(modifier = Modifier.fillMaxSize()) {
            ProductScanningScreen(
                viewModel = viewModel, // ✅ Pass ViewModel here
                onClose = onClose,
                onConfirm = onConfirm
            )

            IconButton(
                onClick = { onClose() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close")
            }
        }
    } else {
        Text("Camera permission required!", Modifier.padding(16.dp))
    }
}


@SuppressLint("RestrictedApi")
@Composable
fun CameraScreen(
    capturedImageUri: Uri?,
    onImageCaptured: (Uri) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    var previewView: PreviewView? by remember { mutableStateOf(null) }
    var isFlashOn by remember { mutableStateOf(false) }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    LaunchedEffect(capturedImageUri) {
        if (capturedImageUri == null) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView?.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageCapture
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (capturedImageUri == null) {
            // Show live camera preview
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        previewView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Scan overlay
            Box(modifier = Modifier
                .matchParentSize()
                .zIndex(1f)) {
                ScanOverlayWithTransparentHoleAndPulse()
            }

            // Scan + Flash buttons
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2f)
                    .padding(45.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier
                        .padding(bottom = 120.dp)
                ) {
                    // scan Button
                    Button(
                        onClick = {
                            val outputFile =
                                File(context.externalCacheDir, "${System.currentTimeMillis()}.jpg")
                            val outputOptions =
                                ImageCapture.OutputFileOptions.Builder(outputFile).build()
                            imageCapture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        val uri = Uri.fromFile(outputFile)
                                        onImageCaptured(uri)
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e(
                                            "Camera",
                                            "Image capture failed: ${exception.message}"
                                        )
                                    }
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.zIndex(3f)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_document_scanner_24),
                            contentDescription = "Scan Button",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // flash Button
                    Button(
                        onClick = {
                            isFlashOn = !isFlashOn
                            Toast.makeText(
                                context,
                                if (isFlashOn) "Flash On" else "Flash Off",
                                Toast.LENGTH_SHORT
                            ).show()
                            imageCapture.camera?.cameraControl?.enableTorch(isFlashOn)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        modifier = Modifier.zIndex(3f)
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isFlashOn) R.drawable.baseline_flashlight_off_24 else R.drawable.baseline_flashlight_on_24
                            ),
                            contentDescription = "Flash Toggle",
                            tint = Color.White, modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        } else {
            AsyncImage(
                model = capturedImageUri,
                contentDescription = "Captured Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
        }
    }
}


@Composable
fun ScanOverlayWithTransparentHoleAndPulse() {
    // نسبة العرض إلى الارتفاع للفاتورة (مثال 9:16)
    val invoiceAspectRatio = 9f / 16f

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxW = maxWidth
        val maxH = maxHeight

        // عرض الصندوق 80% من عرض الشاشة
        val boxWidth  = maxW * 0.8f
        // ارتفاعه بحساب النسبة
        val boxHeight = boxWidth / invoiceAspectRatio

        // إذا تجاوز الارتفاع الشاشة، خفّضه
        val finalHeight = minOf(boxHeight, maxH * 0.95f)
        val finalWidth  = finalHeight * invoiceAspectRatio

        val cornerRadius = 16.dp

        // أنيمشن النبض
        val infiniteTransition = rememberInfiniteTransition()
        val animatedBorderAlpha by infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = EaseInQuart),
                repeatMode = RepeatMode.Reverse
            )
        )

        // الخلفية المظلّلة مع فتحة شفافة
        Box(modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                // مسار الفتحة
                val left   = (size.width - finalWidth.toPx()) / 2
                val top    = (size.height - finalHeight.toPx()) / 2
                val right  = left + finalWidth.toPx()
                val bottom = top + finalHeight.toPx()
                val holePath = Path().apply {
                    addRoundRect(RoundRect(
                        Rect(left, top, right, bottom),
                        CornerRadius(cornerRadius.toPx())
                    ))
                }
                onDrawWithContent {
                    drawContent()
                    drawRect(Color.Black.copy(alpha = 0.6f), size = size)
                    drawPath(holePath, Color.Transparent, blendMode = BlendMode.Clear)
                }
            }
        )

        // إطار الفحص والنبض
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            Box(modifier = Modifier
                .size(finalWidth, finalHeight)
                .border(
                    width = 2.dp,
                    color = Color.White.copy(alpha = animatedBorderAlpha),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .align(Alignment.Center)
            )

            Text(
                text = "Align the product inside the box",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = finalHeight / 2 + 24.dp)
            )
        }
    }
}



fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun getBitmapSizeInMB(bitmap: Bitmap): Double {
    var byteCount = bitmap.byteCount
    return byteCount.toDouble() / (1024 * 1024)
}

suspend fun extractDescriptionFromImage(
    bitmap: Bitmap,
    onTextExtracted: (String) -> Unit,
    onInvoiceParsed: (InvoiceResult?) -> Unit
): String {
    val buildConfig = BuildConfig()

    val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = buildConfig.apiKey
    )
    val image = reduceBitmapSize(bitmap)
    val cropedImage = cropCenterOfBitmap(image, 1280, 1280)

    val inputContent = content {
        image(cropedImage)
        text(
            """
               Your response should be a clean, valid JSON matching the format above.
                Do NOT include any markdown formatting like ```json or ```
            You are an intelligent invoice parser. Given the raw text extracted from an image, your task is to extract the following structured data from the invoice content only:

            Fields to extract:
            date: The date of the invoice (format: yyyy-dd-MM or similar)
            total: Final amount paid (e.g., "1000 DZD")
            type: Type of purchase, strictly from:
            ["Health", "Food", "Workout", "Apparel", "Education", "Gifts", "Transport", "Other"]
            
            items: A list of purchased items. Each item should contain:
            name: Item name or description
            quantity: Number of units purchased (if missing, assume 1)
            price: Price per unit (as a number, e.g., 200.00)
            totalPrice: the Total price.

            vendor: A nested object with:
            name: Name of the vendor or store
            address: Address of the store (if not found, return empty string)
            
            Output Format:
            {
              "vendor": {
                "name": "Fashion Store",
                "address": "123 Rue Didouche Mourad, Algiers"
              },
              "date": "2025-04-15",
              "total": "1000 DA",
              "type": "Apparel",
              "items": [
                {
                  "name": "T-shirt",
                  "quantity": 2,
                  "price": 300.00,
                  "totalPrice": 600.00
                }
              ]
            }

            Important Instructions:
    - Your response must be pure JSON only
    - Never use markdown formatting
    - Date format must be yyyy-MM-dd
    - If date parsing fails, use today's date
    - Amounts should be numbers only (without currency symbols)
            """
        )
    }
    var extractedText = ""
    val timeTaken = measureTimeMillis {
        val response = generativeModel.generateContent(inputContent)
        extractedText = response.text ?: ""
        Log.d("gemini", "Text + Image Prompt Result: $extractedText")
        onTextExtracted(extractedText)
    }

    val invoiceResult: InvoiceResult? = try {
        // تنظيف النص باستخدام regex
        val jsonRegex = """(?s)\{.*\}""".toRegex()
        val cleanedText = jsonRegex.find(extractedText)?.value?.let {
            it.replace("```json", "")
                .replace("```", "")
                .trim()
        } ?: extractedText

        val parsed = Json.decodeFromString<InvoiceResult>(cleanedText)

        parsed.copy(items = parsed.items.map { item ->
            item.copy(totalPrice = item.quantity * item.price)
        })

    } catch (e: Exception) {
        Log.e("JSON Parsing Error", "Error: ${e.message}\nText: $extractedText")
        null
    }

    // تمرير النتيجة إلى callback
    onInvoiceParsed(invoiceResult)

    return extractedText
}

fun cropCenterOfBitmap(
    originalBitmap: Bitmap,
    cropWidth: Int,
    cropHeight: Int
): Bitmap {
    val originalWidth = originalBitmap.width
    val originalHeight = originalBitmap.height

    val width = cropWidth.coerceAtMost(originalWidth)
    val height = cropHeight.coerceAtMost(originalHeight)

    val startX = (originalWidth - width)
    val startY = (originalHeight - height)

    return Bitmap.createBitmap(originalBitmap, startX, startY, width, height)
}


fun reduceBitmapSize(
    bitmap: Bitmap,
    quality: Int = 50,
    maxWidth: Int = 1280,
    maxHeight: Int = 1280
): Bitmap {

    var imageSizeBefore = getBitmapSizeInMB(bitmap)
    Log.d("gemini", " Image Size Before: $imageSizeBefore")

    val ratio: Float = minOf(
        maxWidth.toFloat() / bitmap.width,
        maxHeight.toFloat() / bitmap.height,
        1f
    )
    val newWidth = (bitmap.width * ratio).toInt()
    val newHeight = (bitmap.height * ratio).toInt()

    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

    val outputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

    val compressedBytes = outputStream.toByteArray()

    val compressedBitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
    var imageSizeAfter = getBitmapSizeInMB(compressedBitmap)

    Log.d("gemini", " Image Size After: $imageSizeAfter")

    return BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
}


@RequiresApi(Build.VERSION_CODES.O)
fun tryParseDate(dateStr: String): LocalDate? {
    val formatters = listOf(
        DateTimeFormatter.ofPattern("yyyy-dd-MM"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ISO_LOCAL_DATE
    )

    return formatters.firstNotNullOfOrNull { formatter ->
        try {
            LocalDate.parse(dateStr, formatter)
        } catch (e: Exception) {
            null
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProductScanningScreen(
    viewModel: TransactionViewModel,
    onClose: () -> Unit,
    onConfirm: () -> Unit
) {
    var extractedText by remember { mutableStateOf("") }
    var invoiceResult by remember { mutableStateOf<InvoiceResult?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraScreen(
            capturedImageUri = capturedImageUri,
            onImageCaptured = { imageUri ->
                capturedImageUri = imageUri
                val bitmap = getBitmapFromUri(context, imageUri)

                isLoading = true
                coroutineScope.launch {
                    if (bitmap != null) {
                        try {
                            extractDescriptionFromImage(
                                bitmap,
                                onTextExtracted = { text ->
                                    extractedText = text
                                },
                                onInvoiceParsed = { parsedInvoice -> // Receive parsed invoice data
                                    invoiceResult = parsedInvoice
                                    isLoading = false
                                    showConfirmationDialog = true
                                }
                            )
                        } catch (e: Exception) {
                            Log.e("gemini", "Product Extraction Failed! Please try again",e)
                            isLoading = false // Ensure loading is stopped on error
                        }
                    } else {
                        isLoading = false // Handle null bitmap case
                    }
                }
            },
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .zIndex(100f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp), color = Color.White)
            }
        }

        IconButton(
            onClick = { onClose() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Close",
                tint = Color.White
            )
        }
    }

    if (showConfirmationDialog) {
        var editedText by remember { mutableStateOf(extractedText) }

        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirm Invoice Details") },
            text = {
                Column {
                    capturedImageUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Captured product description",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { editedText = it }, // Capture change event of the edit text.
                        label = { Text("Product Description") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        invoiceResult?.let { result ->
                            val parsedDate = tryParseDate(result.date) ?: run {
                                Toast.makeText(context, "Invalid date format, using invoice date as-is", Toast.LENGTH_LONG).show()
                                LocalDate.parse(result.date, DateTimeFormatter.ISO_LOCAL_DATE)
                            }

                            val safeTotal = result.total.takeIf { it > 0 } ?: result.items.sumOf { it.totalPrice }

                            viewModel.addTransaction(
                                amount = safeTotal,
                                isExpense = true,
                                category = result.type,
                                date = parsedDate,
                                manualDescription = editedText,
                                invoiceDetails = result
                            )
                        }
                        showConfirmationDialog = false
                        onConfirm()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        capturedImageUri = null
                        invoiceResult = null
                    }
                ) {
                    Text("Retake")
                }
            }
        )
    }
}
