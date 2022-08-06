package stephane.amstrong.kotlinmvixxlstore.presentation.category.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Category
import stephane.amstrong.kotlinmvixxlstore.databinding.RowCategoryBinding

class CategoryAdapter(
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "CategoryAdapter"

    private val diffCallback = object : DiffUtil.ItemCallback<Category>() {

        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CategoryVH(
            RowCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            interaction = interaction,
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryVH -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(categories: List<Category>?) {
        val newList = categories?.toMutableList()
        differ.submitList(newList)
    }

    class CategoryVH
    constructor(
        private val binding: RowCategoryBinding,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, category)
            }

            binding.txtName.text = category.name
            //binding.row.isChecked = category.isSelected
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, category: Category)
    }
}